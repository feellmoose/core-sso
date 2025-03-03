# Pomelo SSO

### Package

本项目中打包生成的可用文件在项目根目录的 `target` 目录下，命名为 `pomelo-sso-${project.version}-fat.jar`，直接使用 `jdk-17` 环境运行即可，可以携带 `-Dprofiles.active=prod` 来指定使用配置文件，携带 `-Dprofiles.path=/` 来指定使用配置文件。

例：`java  "-Dprofiles.active=local"  "-Dprofiles.path=E:\conf\" -jar pomelo-sso-1.0.0-fat.jar`

