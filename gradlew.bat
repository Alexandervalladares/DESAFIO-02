@ECHO OFF

SETLOCAL

SET DIR=%~dp0
IF NOT DEFINED JAVA_HOME (
  SET JAVA_EXE=java
) ELSE (
  SET JAVA_EXE=%JAVA_HOME%\bin\java.exe
)

IF NOT EXIST "%JAVA_EXE%" (
  ECHO ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH. 1>&2
  EXIT /B 1
)

SET CLASSPATH=%DIR%\gradle\wrapper\gradle-wrapper.jar

"%JAVA_EXE%" -classpath "%CLASSPATH%" org.gradle.wrapper.GradleWrapperMain %*
