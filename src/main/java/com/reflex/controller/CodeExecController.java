package com.reflex.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.reflex.request.SolutionRequest;
import com.reflex.request.UserTaskRequest;
import com.reflex.response.UnitTestResultResponse;
import com.reflex.request.UnitTestRequest;

import jakarta.validation.Valid;

@CrossOrigin
@RestController
@RequestMapping("/api/exec-module")
public class CodeExecController {
	

	@PostMapping("/setup")
	public ResponseEntity<?> setupProjectsFolder(){
		
		// define OS
		String fs = System.getProperty("file.separator");
    	boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
    	boolean isLinux = System.getProperty("os.name").toLowerCase().startsWith("linux");
    	
    	Path path = Paths.get(System.getProperty("user.home") + fs + "cadidate_test_system_projects");
    	ProcessBuilder processBuilder = new ProcessBuilder();
    	
    	if(Files.exists(path)==false) {
    		
    		// create main, test_launch and lib folders
	    	processBuilder.directory(new File(System.getProperty("user.home")));
	    	if (isWindows) {
	    		processBuilder.command("cmd.exe", "/c", "mkdir cadidate_test_system_projects" + fs + "test_launch" + " " + "cadidate_test_system_projects" + fs
	    				+ "lib");
	    	} 
	    	else if (isLinux){
	    		processBuilder.command("sh", "-c", "mkdir cadidate_test_system_projects" + fs + "test_launch" + " " + "cadidate_test_system_projects" + fs
	    				+ "lib");
	    	}
	    	runProcess(processBuilder);

	        // download JUnit 5 jars
	    	processBuilder.directory(new File(System.getProperty("user.home")));
	    	if (isWindows) {
	    		processBuilder.command("cmd.exe", "/c", "mvn org.apache.maven.plugins:maven-dependency-plugin:3.6.0:get" +
	    	" -DrepoUrl=https://download.java.net/maven/2/ -Dartifact=org.junit.jupiter:junit-jupiter-api:5.9.2");
	    	} 
	    	else if (isLinux){
	    		processBuilder.command("sh", "-c", "mvn org.apache.maven.plugins:maven-dependency-plugin:3.6.0:get" +
	    		    	" -DrepoUrl=https://download.java.net/maven/2/ -Dartifact=org.junit.jupiter:junit-jupiter-api:5.9.2");
	    	}
	    	runProcess(processBuilder);
	        
	    	if (isWindows) {
	    		processBuilder.command("cmd.exe", "/c", "mvn org.apache.maven.plugins:maven-dependency-plugin:3.6.0:get" +
	    	" -DrepoUrl=https://download.java.net/maven/2/ -Dartifact=org.junit.jupiter:junit-jupiter-api:5.9.2");
	    	} 
	    	else if (isLinux){
	    		processBuilder.command("sh", "-c", "mvn org.apache.maven.plugins:maven-dependency-plugin:3.6.0:get" +
	    	" -DrepoUrl=https://download.java.net/maven/2/ -Dartifact=org.junit.platform:junit-platform-console-standalone:1.9.3");
	    	}
	    	runProcess(processBuilder);
	    	
	    	// copy jars to lib folder
	    	if (isWindows) {
	    		processBuilder.command("cmd.exe", "/c", "copy .m2" + fs + "repository" + fs + "org" + fs + "apiguardian" + fs + "apiguardian-api" + fs 
	    				+ "1.1.2" + fs + "apiguardian-api-1.1.2.jar" + " cadidate_test_system_projects" + fs + "lib");
	    	} 
	    	else if (isLinux){
	    		processBuilder.command("sh", "-c", "cp .m2" + fs + "repository" + fs + "org" + fs + "apiguardian" + fs + "apiguardian-api" + fs 
	    				+ "1.1.2" + fs + "apiguardian-api-1.1.2.jar" + " cadidate_test_system_projects" + fs + "lib");
	    	}
	    	runProcess(processBuilder);
	    	if (isWindows) {
	    		processBuilder.command("cmd.exe", "/c", "copy .m2" + fs + "repository" + fs + "org" + fs + "junit" + fs + "jupiter" + fs + "junit-jupiter-api" + fs
	    				+ "5.9.2" + fs + "junit-jupiter-api-5.9.2.jar" + " cadidate_test_system_projects" + fs + "lib");
	    	} 
	    	else if (isLinux){
	    		processBuilder.command("sh", "-c", "cp .m2" + fs + "repository" + fs + "org" + fs + "junit" + fs + "jupiter" + fs + "junit-jupiter-api" + fs
	    				+ "5.9.2" + fs + "junit-jupiter-api-5.9.2.jar" + " cadidate_test_system_projects" + fs + "lib");
	    	}
	    	runProcess(processBuilder);
	    	if (isWindows) {
	    		processBuilder.command("cmd.exe", "/c", "copy .m2" + fs + "repository" + fs + "org" + fs + "junit" + fs + "platform" + fs + "junit-platform-commons"
	    	+ fs + "1.9.2" + fs + "junit-platform-commons-1.9.2.jar" + " cadidate_test_system_projects" + fs + "lib");
	    	} 
	    	else if (isLinux){
	    		processBuilder.command("sh", "-c", "cp .m2" + fs + "repository" + fs + "org" + fs + "junit" + fs + "platform" + fs + "junit-platform-commons"
	    		    	+ fs + "1.9.2" + fs + "junit-platform-commons-1.9.2.jar" + " cadidate_test_system_projects" + fs + "lib");
	    	}
	    	runProcess(processBuilder);
	    	if (isWindows) {
	    		processBuilder.command("cmd.exe", "/c", "copy .m2" + fs + "repository" + fs +"org" + fs +"junit" + fs +"platform" + fs +
	    				"junit-platform-console-standalone" + fs +"1.9.3" + fs + "junit-platform-console-standalone-1.9.3.jar" +
	    				" cadidate_test_system_projects" + fs + "lib");
	    	} 
	    	else if (isLinux){
	    		processBuilder.command("sh", "-c", "cp .m2" + fs + "repository" + fs +"org" + fs +"junit" + fs +"platform" + fs +
	    				"junit-platform-console-standalone" + fs +"1.9.3" + fs + "junit-platform-console-standalone-1.9.3.jar" +
	    				" cadidate_test_system_projects" + fs + "lib");
	    	}
	    	runProcess(processBuilder);
	    	if (isWindows) {
	    		processBuilder.command("cmd.exe", "/c", "copy .m2" + fs + "repository" + fs +"org" + fs + "opentest4j" + fs + "opentest4j" + fs +
	    				"1.2.0" + fs +"opentest4j-1.2.0.jar" + " cadidate_test_system_projects" + fs + "lib");
	    	} 
	    	else if (isLinux){
	    		processBuilder.command("sh", "-c", "cp .m2" + fs + "repository" + fs +"org" + fs + "opentest4j" + fs + "opentest4j" + fs +
	    				"1.2.0" + fs +"opentest4j-1.2.0.jar" + " cadidate_test_system_projects" + fs + "lib");
	    	}
	    	runProcess(processBuilder);
    	}
    	else {
    		System.out.println("main directory already created, skipping...");
    	}
		return new ResponseEntity<>(HttpStatus.CREATED);
	}
	
	@PostMapping("/test-launch")
	public ResponseEntity<UnitTestResultResponse> runUserUnitTest(@Valid @RequestBody UserTaskRequest userTaskRequest){
		// define OS
		String fs = System.getProperty("file.separator");
    	boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
    	boolean isLinux = System.getProperty("os.name").toLowerCase().startsWith("linux");
		ProcessBuilder processBuilder = new ProcessBuilder();
		
	    // create project folder
		Instant submitDate = Instant.now();
		String projectKey = submitDate.toString();
		int index = 0;
		while(index != -1) {
			index = projectKey.indexOf(":");
			if(index != -1) {
				projectKey = projectKey.substring(0, index) + "_" + projectKey.substring(index+1, projectKey.length());
			}
		}
		index = 0;
		while(index != -1) {
			index = projectKey.indexOf("-");
			if(index != -1) {
				projectKey = projectKey.substring(0, index) + "_" + projectKey.substring(index+1, projectKey.length());
			}
		}
		index = projectKey.indexOf(".");
		projectKey = projectKey.substring(0, index) + "_" + projectKey.substring(index+1, projectKey.length());
		
	    processBuilder.directory(new File(System.getProperty("user.home") + fs + "cadidate_test_system_projects" + fs + "test_launch"));
	    if (isWindows) {
	    	processBuilder.command("cmd.exe", "/c", "mkdir " + projectKey + fs + "src" + fs + "main" + fs + "java" + fs + "com" + fs + "cleverhire" + fs
	    			+ "java_project" + projectKey + " " + projectKey + fs + "src" + fs + "test" + fs + "java" + fs + "com" + fs + "cleverhire" + fs
	    			+ "java_project" + projectKey + " " + projectKey + fs + "target" + fs + "classes" + " " + projectKey + fs + "target" + fs + "test_classes");
	    } 
	    else if (isLinux){
	    	processBuilder.command("sh", "-c", "mkdir " + projectKey + fs + "src" + fs + "main" + fs + "java" + fs + "com" + fs + "cleverhire" + fs
	    			+ "java_project" + projectKey + " " + projectKey + fs + "src" + fs + "test" + fs + "java" + fs + "com" + fs + "cleverhire" + fs
	    			+ "java_project" + projectKey + " " + projectKey + fs + "target" + fs + "classes" + " " + projectKey + fs + "target" + fs + "test_classes");
	    }
	    runProcess(processBuilder);
	 
	 // create main files	        
        for(SolutionRequest iterator: userTaskRequest.getSolution()) {
        
	        // add package
	        String codeBase64 = iterator.getCode();
	        byte[] decodedBytes = Base64.getDecoder().decode(codeBase64);
	        String codeDecoded = new String(decodedBytes);
	        codeDecoded = "package com.cleverhire.java_project" + projectKey + ";" + "\n" + codeDecoded;
	        
	        // find out class name
	        int startIndex = codeDecoded.indexOf("class");
	        int secondSpaceIndex = codeDecoded.indexOf(" ", startIndex + 6);
	        String className = codeDecoded.substring(startIndex + 6, secondSpaceIndex);

	        // create className.java
	        FileOutputStream fos;
			try {
				fos = new FileOutputStream(System.getProperty("user.home") + fs + "cadidate_test_system_projects"+ fs + "test_launch" + fs + projectKey + fs
						+ "src" + fs + "main" + fs + "java" + fs + "com" + fs + "cleverhire" + fs + "java_project" + projectKey + fs + className +".java");
				fos.write(codeDecoded.getBytes());
		        fos.flush();
		        fos.close(); 
			} catch (IOException e) {
				e.printStackTrace();
				throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
			}
        }
        
     // create test files
        List<String> testClassesNameList = new ArrayList<>();
        for(UnitTestRequest iterator: userTaskRequest.getUnitTest()) {
        	
	        // add package
	        String codeBase64 = iterator.getCode();
	        byte[] decodedBytes = Base64.getDecoder().decode(codeBase64);
	        String codeDecoded = new String(decodedBytes);
	        codeDecoded = "package com.cleverhire.java_project" + projectKey + ";" + "\n" + codeDecoded;
	        
	        // find out class name
	        int startIndex = codeDecoded.indexOf("class");
	        int secondSpaceIndex = codeDecoded.indexOf(" ", startIndex + 6);
	        String className = codeDecoded.substring(startIndex + 6, secondSpaceIndex);
	        testClassesNameList.add(className);

	        // create className.java
	        FileOutputStream fos;
			try {
				fos = new FileOutputStream(System.getProperty("user.home") + fs + "cadidate_test_system_projects"+ fs + "test_launch" + fs + projectKey + fs
						+ "src" + fs + "test" + fs + "java" + fs + "com" + fs + "cleverhire" + fs + "java_project" + projectKey + fs + className +".java");
				fos.write(codeDecoded.getBytes());
		        fos.flush();
		        fos.close(); 
			} catch (IOException e) {
				e.printStackTrace();
				throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
			}
        }
        
      // compile classes
        String testsResult="";
        boolean compileIsOk = true;
        UnitTestResultResponse response = new UnitTestResultResponse();
        // compile src
	    processBuilder.directory(new File(System.getProperty("user.home") + fs + "cadidate_test_system_projects" + fs + "test_launch" + fs + projectKey));
	    if (isWindows) {
	    	processBuilder.command("cmd.exe", "/c", "javac -d target" + fs + "classes" + " src" + fs + "main" + fs + "java" + fs + "com" + fs + "cleverhire" + fs 
	    			+ "java_project" + projectKey + fs + "*.java");
	    } 
	    else if (isLinux){
	    	processBuilder.command("sh", "-c", "javac -d target" + fs + "classes" + " src" + fs + "main" + fs + "java" + fs + "com" + fs + "cleverhire" + fs 
	    			+ "java_project" + projectKey + fs + "*.java");
	    }
	    String mainCompileResult = runProcess(processBuilder);
	    
	    // check compile errors
	    int errorCodeIndex = mainCompileResult.indexOf("error code");
	    Integer errorCode = Integer.parseInt(mainCompileResult.substring(errorCodeIndex+13 ,errorCodeIndex+14));	    
	    if(errorCode == 1) {
	    	compileIsOk = false;
	    	testsResult = mainCompileResult;
	    	response.setCompileIsOk(false);
   
	  	  	// delete created folder
		    processBuilder.directory(new File(System.getProperty("user.home") + fs + "cadidate_test_system_projects" + fs + "test_launch"));
		    if (isWindows) {
		    	processBuilder.command("cmd.exe", "/c", "rmdir /s /q " + projectKey);
		    } 
		    else if (isLinux){
		    	processBuilder.command("sh", "-c", "rm -rf " + projectKey);
		    }
		    runProcess(processBuilder);
	        
	    }
	    
	    if(compileIsOk) {
	    	
		  // compile test
		    processBuilder.directory(new File(System.getProperty("user.home") + fs + "cadidate_test_system_projects" + fs + "test_launch" + fs + projectKey));
		    if (isWindows) {
		    	processBuilder.command("cmd.exe", "/c", "javac -d target" + fs + "test_classes" + " -cp %USERPROFILE%" + fs + "cadidate_test_system_projects" + fs 
		    			+ "lib" + fs + "*;target" + fs + "classes" + " src" + fs + "test" + fs + "java" + fs + "com" + fs + "cleverhire" + fs + "java_project" 
		    			+ projectKey + fs + "*.java");
		    } 
		    else if (isLinux){
		    	processBuilder.command("sh", "-c", "javac -d target"+ fs + "classes" + fs + "test_classes" + " -cp ~" + fs + "cadidate_test_system_projects" + fs
		    			+ "lib" + fs + "*;target" + " src" + fs + "test" + fs + "java" + fs + "com" + fs + "cleverhire" + fs + "java_project" + projectKey + fs + "*.java");
		    }
		    String testCompileResult = runProcess(processBuilder);
		    
		    // check compile errors
		    errorCodeIndex = testCompileResult.indexOf("error code");
		    errorCode = Integer.parseInt(testCompileResult.substring(errorCodeIndex+13 ,errorCodeIndex+14));	    
		    if(errorCode == 1) {
		    	compileIsOk = false;
		    	testsResult = testCompileResult;
		    	response.setCompileIsOk(false);
	     
		  	  	// delete created folder
			    processBuilder.directory(new File(System.getProperty("user.home") + fs + "cadidate_test_system_projects" + fs + "test_launch"));
			    if (isWindows) {
			    	processBuilder.command("cmd.exe", "/c", "rmdir /s /q " + projectKey);
			    } 
			    else if (isLinux){
			    	processBuilder.command("sh", "-c", "rm -rf " + projectKey);
			    }
			    runProcess(processBuilder);	      
		    }
	    }
	    
	    if(compileIsOk) {
	    	
	  // run unit tests
		    for(String testClassName: testClassesNameList) {
			    if (isWindows) {
			    	processBuilder.command("cmd.exe", "/c", "java -jar" + " %USERPROFILE%" + fs + "cadidate_test_system_projects" + fs + "lib" + fs
			    			+ "junit-platform-console-standalone-1.9.3.jar" + " --class-path" + " target" + fs + "classes;target" + fs + "test_classes" 
			    			+ " --select-class" + " com.cleverhire." + "java_project" + projectKey + "." + testClassName);
			    } 
			    else if (isLinux){
			    	processBuilder.command("sh", "-c", "java -jar" + " ~" + fs + "cadidate_test_system_projects" + fs + "lib" + fs
			    			+ "junit-platform-console-standalone-1.9.3.jar" + " --class-path" + " target"  + fs + "classes;target" + fs + "test_classes" 
			    			+ " --select-class" + " com.cleverhire." + "java_project" + projectKey + "." + testClassName);
			    }
			    testsResult = testsResult + runProcess(processBuilder);
		    }
		    response.setCompileIsOk(true);
	    }
	  // delete created folder
	    processBuilder.directory(new File(System.getProperty("user.home") + fs + "cadidate_test_system_projects" + fs + "test_launch"));
	    if (isWindows) {
	    	processBuilder.command("cmd.exe", "/c", "rmdir /s /q " + projectKey);
	    } 
	    else if (isLinux){
	    	processBuilder.command("sh", "-c", "rm -rf " + projectKey);
	    }
	    runProcess(processBuilder);

        String encodedtestResult = Base64.getEncoder().encodeToString(testsResult.getBytes(StandardCharsets.ISO_8859_1));
        response.setResult(encodedtestResult);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	public String runProcess(ProcessBuilder processBuilder) {
		String result = "";
		String errorMsg = "";
        try {
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String line=null;
            String error=null;
            while ( ((line = reader.readLine()) != null) || ((error = errorReader.readLine()) != null) ) {
            	if (line != null) {
                	result = result + "\n" + "~" + line;
                    System.out.println(line);
            	}
            	if (error!=null) {
            		errorMsg = errorMsg + "\n" + "~" + error;
                    System.out.println(error);
            	}
            }
            int exitCode = process.waitFor();
            System.out.println("\nExited with error code : " + exitCode);
            result = result + errorMsg + "\n ~ Exited with error code : " + exitCode;
            return result;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);  
        }

	}

}
