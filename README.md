# AxiFi (by CyberDyne)

A financial tracking program.

## Releases (hosted on Google Drive)
**Latest:**

| OS        | Installer           |
| ------------- |:-------------:|
| Windows (.exe)      | [AxiFi.zip](https://github.com/mccallum-sgd/AxiFi/blob/master/build/deploy/bundles/AxiFi-3.0.zip?raw=true) |
| MacOS      | Currently unavailable      |
| Linux | Currently unavailable       |
| All (JAR) | [AxiFi.zip](https://github.com/mccallum-sgd/AxiFi/blob/master/build/deploy/AxiFi.zip?raw=true) |

## Developers

### Testing
Can be run as a normal Java application in eclipse. The main method is in **MainApp.java**.

### Deployment
This project uses the  [e(fx)clipse](https://www.eclipse.org/efxclipse/index.html) Eclipse extension, and subsequently [Apache Ant](https://ant.apache.org/), to generate various installers for Windows (.exe), MacOS (.dmg), and Linux (.deb).

After installing e(fx)clipse, the installer can be built by opening **build.fxbuild** and clicking "_Generate ant build.xml and run_" in the **Building_Exporting** section on the top right.

This will generate the installers in **\build\deploy**.
