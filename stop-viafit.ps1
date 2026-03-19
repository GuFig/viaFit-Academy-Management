$ErrorActionPreference = 'Stop'

$projectRoot = Split-Path -Parent $MyInvocation.MyCommand.Path

if ($env:WILDFLY_HOME -and (Test-Path (Join-Path $env:WILDFLY_HOME 'bin\jboss-cli.ps1'))) {
    $wildFlyHome = $env:WILDFLY_HOME
} else {
    $wildFlyHome = Join-Path $projectRoot '.runtime\wildfly-26.1.3.Final'
}

$cliScript = Join-Path $wildFlyHome 'bin\jboss-cli.ps1'

if (-not (Test-Path $cliScript)) {
    throw 'WildFly nao encontrado. Defina WILDFLY_HOME ou use a pasta .runtime local.'
}

$previousPreference = $ErrorActionPreference
$script:ErrorActionPreference = 'Continue'
try {
    & $cliScript --connect --command=':shutdown'
    if ($LASTEXITCODE -ne 0) {
        throw 'WildFly nao estava acessivel.'
    }
} finally {
    $script:ErrorActionPreference = $previousPreference
}

Write-Host 'WildFly finalizado com sucesso.'
