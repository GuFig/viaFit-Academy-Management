$ErrorActionPreference = 'Stop'

$projectRoot = Split-Path -Parent $MyInvocation.MyCommand.Path
$dbHost = if ($env:DB_HOST) { $env:DB_HOST } else { 'localhost' }
$dbPort = if ($env:DB_PORT) { $env:DB_PORT } else { '5432' }
$dbName = if ($env:DB_NAME) { $env:DB_NAME } else { 'academy_management' }
$dbUser = if ($env:DB_USER) { $env:DB_USER } else { 'postgres' }
$dbPassword = if ($env:DB_PASSWORD) { $env:DB_PASSWORD } else { '12345' }

function Resolve-Maven {
    $maven = (Get-Command mvn.cmd -ErrorAction SilentlyContinue | Select-Object -First 1 -ExpandProperty Source)
    if ($maven) {
        return $maven
    }

    $cached = Get-ChildItem "$env:USERPROFILE\.m2\wrapper\dists\apache-maven-*" -Recurse -Filter mvn.cmd -ErrorAction SilentlyContinue |
        Select-Object -First 1 -ExpandProperty FullName
    if ($cached) {
        return $cached
    }

    throw 'Maven nao encontrado. Instale o Maven ou adicione mvn.cmd ao PATH.'
}

function Resolve-WildFlyHome {
    if ($env:WILDFLY_HOME) {
        $candidate = Join-Path $env:WILDFLY_HOME 'bin\standalone.bat'
        if (Test-Path $candidate) {
            return $env:WILDFLY_HOME
        }
    }

    $local = Join-Path $projectRoot '.runtime\wildfly-26.1.3.Final'
    if (Test-Path (Join-Path $local 'bin\standalone.bat')) {
        return $local
    }

    throw 'WildFly nao encontrado. Defina WILDFLY_HOME ou extraia o servidor em .runtime\wildfly-26.1.3.Final.'
}

function Resolve-PostgresJar {
    $jar = Get-ChildItem "$env:USERPROFILE\.m2\repository\org\postgresql\postgresql" -Recurse -Filter 'postgresql-*.jar' -ErrorAction SilentlyContinue |
        Sort-Object FullName -Descending |
        Select-Object -First 1 -ExpandProperty FullName

    if (-not $jar) {
        throw 'Driver JDBC do PostgreSQL nao encontrado no repositorio Maven local.'
    }

    return $jar
}

function Test-Cli {
    param(
        [string]$CliScript
    )

    $previousPreference = $ErrorActionPreference
    $script:ErrorActionPreference = 'Continue'
    try {
        & $CliScript --connect --command=':read-attribute(name=server-state)' *> $null
        return ($LASTEXITCODE -eq 0)
    } finally {
        $script:ErrorActionPreference = $previousPreference
    }
}

function Wait-Cli {
    param(
        [string]$CliScript,
        [int]$Attempts = 60
    )

    for ($i = 0; $i -lt $Attempts; $i++) {
        if (Test-Cli -CliScript $CliScript) {
            return
        }

        Start-Sleep -Seconds 2
    }

    throw 'WildFly nao respondeu a tempo.'
}

function Invoke-Cli {
    param(
        [string]$CliScript,
        [string[]]$Arguments,
        [switch]$Quiet
    )

    $previousPreference = $ErrorActionPreference
    $script:ErrorActionPreference = 'Continue'
    try {
        if ($Quiet) {
            & $CliScript @Arguments *> $null
        } else {
            & $CliScript @Arguments
        }
    } finally {
        $script:ErrorActionPreference = $previousPreference
    }

    if ($LASTEXITCODE -ne 0) {
        throw "Falha ao executar o WildFly CLI: $($Arguments -join ' ')"
    }
}

$mavenCmd = Resolve-Maven
$wildFlyHome = Resolve-WildFlyHome
$jbossCliPs1 = Join-Path $wildFlyHome 'bin\jboss-cli.ps1'
$standaloneBat = Join-Path $wildFlyHome 'bin\standalone.bat'
$logDir = Join-Path $projectRoot '.runtime\logs'
$moduleDir = Join-Path $wildFlyHome 'modules\system\layers\base\org\postgresql\main'
$moduleXml = Join-Path $projectRoot 'wildfly\postgresql-module\module.xml'
$postgresJar = Resolve-PostgresJar
$outLog = Join-Path $logDir 'wildfly-out.log'
$errLog = Join-Path $logDir 'wildfly-err.log'
$appUrl = 'http://127.0.0.1:8080/academy-management/alunos.xhtml'

New-Item -ItemType Directory -Force -Path $logDir | Out-Null
New-Item -ItemType Directory -Force -Path $moduleDir | Out-Null
Copy-Item $moduleXml (Join-Path $moduleDir 'module.xml') -Force
Copy-Item $postgresJar (Join-Path $moduleDir 'postgresql-42.7.4.jar') -Force

Write-Host '[1/4] Gerando WAR...'
& $mavenCmd -q clean package
if ($LASTEXITCODE -ne 0) {
    throw 'Falha ao gerar o WAR.'
}

$warPath = (Resolve-Path (Join-Path $projectRoot 'target\academy-management.war')).Path
$warUri = ([System.Uri]$warPath).AbsoluteUri

Write-Host '[2/4] Garantindo WildFly em execucao...'
if (Test-Cli -CliScript $jbossCliPs1) {
    Write-Host 'WildFly ja esta em execucao.'
} else {
    Start-Process -FilePath 'cmd.exe' -ArgumentList '/c', "`"$standaloneBat`" -b 0.0.0.0" -RedirectStandardOutput $outLog -RedirectStandardError $errLog | Out-Null
    Wait-Cli -CliScript $jbossCliPs1
    Write-Host 'WildFly iniciado.'
}

Write-Host '[3/4] Configurando datasource...'
$connectionUrl = "jdbc:postgresql://$dbHost`:$dbPort/$dbName"

$driverExists = $true
try {
    Invoke-Cli -CliScript $jbossCliPs1 -Arguments @('--connect', '--command=/subsystem=datasources/jdbc-driver=postgresql:read-resource') -Quiet
} catch {
    $driverExists = $false
}

if (-not $driverExists) {
    Invoke-Cli -CliScript $jbossCliPs1 -Arguments @('--connect', '--command=/subsystem=datasources/jdbc-driver=postgresql:add(driver-name=postgresql,driver-module-name=org.postgresql)')
}

$dataSourceExists = $true
try {
    Invoke-Cli -CliScript $jbossCliPs1 -Arguments @('--connect', '--command=/subsystem=datasources/data-source=AcademyDS:read-resource') -Quiet
} catch {
    $dataSourceExists = $false
}

if (-not $dataSourceExists) {
    $addDataSource = "/subsystem=datasources/data-source=AcademyDS:add(jndi-name=java:/jdbc/AcademyDS,driver-name=postgresql,connection-url=`"$connectionUrl`",user-name=`"$dbUser`",password=`"$dbPassword`",min-pool-size=5,max-pool-size=20,enabled=true,jta=true,use-ccm=true,valid-connection-checker-class-name=org.jboss.jca.adapters.jdbc.extensions.postgres.PostgreSQLValidConnectionChecker,exception-sorter-class-name=org.jboss.jca.adapters.jdbc.extensions.postgres.PostgreSQLExceptionSorter)"
    Invoke-Cli -CliScript $jbossCliPs1 -Arguments @('--connect', "--command=$addDataSource")
}

Invoke-Cli -CliScript $jbossCliPs1 -Arguments @('--connect', "--command=/subsystem=datasources/data-source=AcademyDS:write-attribute(name=connection-url,value=`"$connectionUrl`")")
Invoke-Cli -CliScript $jbossCliPs1 -Arguments @('--connect', "--command=/subsystem=datasources/data-source=AcademyDS:write-attribute(name=user-name,value=`"$dbUser`")")
Invoke-Cli -CliScript $jbossCliPs1 -Arguments @('--connect', "--command=/subsystem=datasources/data-source=AcademyDS:write-attribute(name=password,value=`"$dbPassword`")")
Invoke-Cli -CliScript $jbossCliPs1 -Arguments @('--connect', '--command=/subsystem=datasources/data-source=AcademyDS:enable')
Invoke-Cli -CliScript $jbossCliPs1 -Arguments @('--connect', '--command=:reload') -Quiet
Wait-Cli -CliScript $jbossCliPs1
Invoke-Cli -CliScript $jbossCliPs1 -Arguments @('--connect', '--command=/subsystem=datasources/data-source=AcademyDS:test-connection-in-pool')

Write-Host '[4/4] Fazendo deploy...'
Invoke-Cli -CliScript $jbossCliPs1 -Arguments @('--connect', "--command=deploy --url=$warUri --name=academy-management.war --force")

Write-Host ''
Write-Host 'viaFit no ar em:'
Write-Host $appUrl
Write-Host ''
Write-Host 'Logs:'
Write-Host $outLog
Write-Host $errLog

Start-Process $appUrl
