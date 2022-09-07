# (c) https://github.com/MontiCore/monticore

# first, make sure powershell is running with admin priviliges

#### START ELEVATE TO ADMIN #####
param(
    [Parameter(Mandatory=$false)]
    [switch]$shouldAssumeToBeElevated,

    [Parameter(Mandatory=$false)]
    [String]$workingDirOverride
)

# If parameter is not set, we are propably in non-admin execution. We set it to the current working directory so that
# the working directory of the elevated execution of this script is the current working directory
if(-not($PSBoundParameters.ContainsKey('workingDirOverride'))) {
    $workingDirOverride = (Get-Location).Path
}

function Test-Admin {
    $currentUser = New-Object Security.Principal.WindowsPrincipal $([Security.Principal.WindowsIdentity]::GetCurrent())
    $currentUser.IsInRole([Security.Principal.WindowsBuiltinRole]::Administrator)
}

if ((Test-Admin) -eq $false)  {
    if ($shouldAssumeToBeElevated) {
        Write-Output "Elevating did not work :("
    } else {
        Start-Process powershell.exe -Verb RunAs -ArgumentList ('-noprofile -noexit -file "{0}" -shouldAssumeToBeElevated -workingDirOverride "{1}"' -f ($myinvocation.MyCommand.Definition, "$workingDirOverride"))
    }
    exit
}

Set-Location "$workingDirOverride"
##### END ELEVATE TO ADMIN #####

# the shell now runs in admin mode


<#
 # checks using "Get-Command" if a specific program is installed on the system
 #
 # @param $ProgramName name of the program that should be checked
 # @return $true if $ProgramName is already installed on the System, $false otherwise
 #>
function Get-IsInstalled {
    param(
        [Parameter(Mandatory)][string]$ProgramName
    )

    # Reload Path Environment Variable
    $env:Path = [System.Environment]::GetEnvironmentVariable("Path","Machine") + ";" + [System.Environment]::GetEnvironmentVariable("Path","User")

    try{
        $ErrorActionPreference = "Stop"
        $CheckCommand = Get-Command $ProgramName
        $ErrorActionPreference = "Continue"
        return $true
    }
    catch {
        $ErrorActionPreference = "Continue"
        return $false
    }
}

<#
 # adds a path to the PATH environment variable
 # @param $PathToAdd path to be added to PATH
 #>
function AddToPath {
    param(
        [Parameter(Mandatory)][string]$PathToAdd
    )

    # modify PATH
    $oldpath = (Get-ItemProperty -Path 'Registry::HKEY_LOCAL_MACHINE\System\CurrentControlSet\Control\Session Manager\Environment' -Name PATH).path
    $newpath="$oldpath;$PathToAdd"
    Set-ItemProperty -Path 'Registry::HKEY_LOCAL_MACHINE\System\CurrentControlSet\Control\Session Manager\Environment' -Name PATH -Value $newPath

    # Reload PATH
    $env:Path = [System.Environment]::GetEnvironmentVariable("Path","Machine") + ";" + [System.Environment]::GetEnvironmentVariable("Path","User")
}

##########################################
# Install WinGet CLI
##########################################
if(-not (Get-IsInstalled winget)){
    # Install RTE
    Invoke-Webrequest -UseBasicParsing -OutFile VCLibs.appx https://aka.ms/Microsoft.VCLibs.x64.14.00.Desktop.appx
    Add-AppxPackage -Path "$PWD\VCLibs.appx"
    rm "$PWD\VCLibs.appx"

    # Find current release
    $data = Invoke-Webrequest -UseBasicParsing https://api.github.com/repos/microsoft/winget-cli/releases/latest
    $data = $data.Content | ConvertFrom-Json

    # Get URL of installer
    foreach($asset in $data[0].assets)
    {
      if ($asset.name.endswith("msixbundle"))
      {
        $wingetUrl=$asset.browser_download_url
      }
    }
    # Download and install
    Invoke-Webrequest -UseBasicParsing -OutFile WinGet.msixbundle -Uri $wingetUrl
    Add-AppPackage -path ".\WinGet.msixbundle"
    rm ".\WinGet.msixbundle"

    # Reload Path Environment Variable
    $env:Path = [System.Environment]::GetEnvironmentVariable("Path","Machine") + ";" + [System.Environment]::GetEnvironmentVariable("Path","User")
}

##########################################
# Install software available via WinGet
##########################################
if(-not (Get-IsInstalled git)){
    winget install -e Git.Git
}
if(-not (Get-IsInstalled java) -or (-not ([string](java --version)).Contains("11"))){
    winget install -e Microsoft.OpenJDK.11
}
if(-not (Get-IsInstalled cmake)){
    winget install -e Kitware.CMake
}
if(-not (Get-IsInstalled docker)){
    winget install -e Docker.DockerDesktop
}
if(-not (Get-IsInstalled mosquitto)){
    winget install -e EclipseFoundation.Mosquitto

    AddToPath("C:\Program Files\Mosquitto")
    AddToPath("C:\Program Files\CMake\bin")
}
# Reload Path Environment Variable
$env:Path = [System.Environment]::GetEnvironmentVariable("Path","Machine") + ";" + [System.Environment]::GetEnvironmentVariable("Path","User")

# Start Mosquitto MQTT Broker
Start-Service -Name Mosquitto

##########################################
# Install Chocolatery Package Manager
##########################################
if(-not (Get-IsInstalled choco)){
    Set-ExecutionPolicy Bypass -Scope Process -Force; [System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072; iex ((New-Object System.Net.WebClient).DownloadString('https://community.chocolatey.org/install.ps1'))
}

##########################################
# Install Maven
##########################################
if(-not (Get-IsInstalled mvn)){
    # Download
    choco install maven

    # Reload Path Environment Variable
    $env:Path = [System.Environment]::GetEnvironmentVariable("Path","Machine") + ";" + [System.Environment]::GetEnvironmentVariable("Path","User")
}

##########################################
# Install Ninja
##########################################
if(-not (Get-IsInstalled ninja)){
    # Download
    Invoke-Webrequest -UseBasicParsing -OutFile Ninja.zip -Uri https://github.com/ninja-build/ninja/releases/download/v1.10.2/ninja-win.zip
    Expand-Archive -DestinationPath 'C:\Program Files\Ninja' Ninja.zip
    rm .\Ninja.zip

    AddToPath("C:\Program Files\Ninja\")
}
##########################################
# Install MinGW
##########################################
if(-not (Get-IsInstalled gcc)){
    choco install -y mingw
}

##########################################
# Install NNG 1.3.0
##########################################
Invoke-Webrequest -UseBasicParsing -OutFile nng.zip -Uri https://github.com/nanomsg/nng/archive/v1.3.0.zip
Expand-Archive -DestinationPath "$PWD" nng.zip
rm .\nng.zip
cd .\nng-1.3.0\
mkdir build
cd .\build\
cmake -G Ninja ..
ninja
ninja test
ninja install
cd ..
cd ..

##########################################
# Install Python
##########################################

if(-not (Get-IsInstalled python))
{
    choco install python

    # Reload Path Environment Variable
    $env:Path = [System.Environment]::GetEnvironmentVariable("Path","Machine") + ";" + [System.Environment]::GetEnvironmentVariable("Path","User")

    python -m pip install -U pip
}

##########################################
# Install Conan
##########################################

if(-not (Get-IsInstalled conan)){
    pip install conan

    # Reload Path Environment Variable
    $env:Path = [System.Environment]::GetEnvironmentVariable("Path","Machine") + ";" + [System.Environment]::GetEnvironmentVariable("Path","User")
}

##########################################
# Install MontiThings
##########################################
mvn clean install "-Dmaven.test.skip=true" "-Dexec.skip"
