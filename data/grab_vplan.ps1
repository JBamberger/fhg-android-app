$date = Get-Date -Format "yyyyMMddHHmmss"
$p1 = "https://www.fhg-radolfzell.de/vertretungsplan/f1/subst_001.htm"
$p2 = "https://www.fhg-radolfzell.de/vertretungsplan/f2/subst_001.htm"

$year = Get-Date -Format "yyyy"
$month = Get-Date -Format "MM"
$day = Get-Date -Format "dd"

$dir = "$PSScriptRoot\$year\$month\$day\"

if (!(Test-Path -Path $dir)) {
    New-Item -Type Directory -Path $dir
}


$progressPreference = 'silentlyContinue'
Invoke-WebRequest $p1 -OutFile "$dir/$($date)_page1.htm"
Invoke-WebRequest $p2 -OutFile "$dir/$($date)_page2.htm"
$progressPreference = 'Continue'