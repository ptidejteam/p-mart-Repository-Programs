; -- Password Tracker Installer --

[Setup]
AppName=Password Tracker
AppVerName=Password Tracker 1.4
DefaultDirName={pf}\patra-1.4
DefaultGroupName=Password Tracker 1.4
UninstallDisplayIcon={app}\patra-1.4.exe
Compression=lzma
SolidCompression=yes
OutputDir=userdocs:Inno Setup Examples Output

[Files]
Source: "..\..\..\..\target\patra-1.4.exe"; DestDir: "{app}"

[Icons]
Name: "{group}\Password Tracker 1.4"; Filename: "{app}\patra-1.4.exe"
Name: "{commondesktop}\Password Tracker 1.4"; Filename: "{app}\patra-1.4.exe"
