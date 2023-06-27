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
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.Timer;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
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
	
	@GetMapping("/check") 
	public ResponseEntity<String> checkAppIsRunning(){
		return new ResponseEntity<>("OK", HttpStatus.OK);
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
		
	    processBuilder.directory(new File(System.getProperty("user.home") + fs + "candidate_test_system_projects" + fs + "test_launch"));
	    if (isWindows) {
	    	processBuilder.command("cmd.exe", "/c", "mkdir " + projectKey + fs + "src" + fs + "main" + fs + "java" + fs + "com" + fs + "cleverhire" + fs
	    			+ "java_project" + projectKey + " " + projectKey + fs + "src" + fs + "test" + fs + "java" + fs + "com" + fs + "cleverhire" + fs
	    			+ "java_project" + projectKey + " " + projectKey + fs + "target" + fs + "classes" + " " + projectKey + fs + "target" + fs + "test_classes");
	    } 
	    else if (isLinux){
	    	processBuilder.command("sh", "-c", "mkdir -p " + projectKey + fs + "src" + fs + "main" + fs + "java" + fs + "com" + fs + "cleverhire" + fs
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
				fos = new FileOutputStream(System.getProperty("user.home") + fs + "candidate_test_system_projects"+ fs + "test_launch" + fs + projectKey + fs
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
				fos = new FileOutputStream(System.getProperty("user.home") + fs + "candidate_test_system_projects"+ fs + "test_launch" + fs + projectKey + fs
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
        List<String> testsResult = new ArrayList<>();
        boolean compileIsOk = true;
        UnitTestResultResponse response = new UnitTestResultResponse();
        // compile src
	    processBuilder.directory(new File(System.getProperty("user.home") + fs + "candidate_test_system_projects" + fs + "test_launch" + fs + projectKey));
	    if (isWindows) {
	    	processBuilder.command("cmd.exe", "/c", "javac -d target" + fs + "classes" + " src" + fs + "main" + fs + "java" + fs + "com" + fs + "cleverhire" + fs 
	    			+ "java_project" + projectKey + fs + "*.java");
	    } 
	    else if (isLinux){
	    	processBuilder.command("sh", "-c", "/opt/jdk-17.0.6/bin/javac -d target" + fs + "classes" + " src" + fs + "main" + fs + "java" + fs + "com" + fs 
	    			+ "cleverhire" + fs + "java_project" + projectKey + fs + "*.java");
	    }
	    String mainCompileResult = runProcess(processBuilder);
	    
	    // check compile errors
	    int errorCodeIndex = mainCompileResult.indexOf("error code");
	    Integer errorCode = Integer.parseInt(mainCompileResult.substring(errorCodeIndex+13 ,errorCodeIndex+14));	    
	    if(errorCode == 1) {
	    	compileIsOk = false;
	    	testsResult.add(mainCompileResult);
	    	response.setCompileIsOk(false);
   
	  	  	// delete created folder
		    processBuilder.directory(new File(System.getProperty("user.home") + fs + "candidate_test_system_projects" + fs + "test_launch"));
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
		    processBuilder.directory(new File(System.getProperty("user.home") + fs + "candidate_test_system_projects" + fs + "test_launch" + fs + projectKey));
		    if (isWindows) {
		    	processBuilder.command("cmd.exe", "/c", "javac -d target" + fs + "test_classes" + " -cp %USERPROFILE%" + fs + "candidate_test_system_projects" + fs 
		    			+ "lib" + fs + "*;target" + fs + "classes" + " src" + fs + "test" + fs + "java" + fs + "com" + fs + "cleverhire" + fs + "java_project" 
		    			+ projectKey + fs + "*.java");
		    } 
		    else if (isLinux){
		    	processBuilder.command("sh", "-c", "/opt/jdk-17.0.6/bin/javac -d target" + fs + "test_classes" + " -cp ~" + fs + "candidate_test_system_projects" + fs
		    			+ "lib" + fs + "*:target" + fs + "classes" + " src" + fs + "test" + fs + "java" + fs + "com" + fs + "cleverhire" + fs + "java_project" 
		    			+ projectKey + fs + "*.java");
		    }
		    String testCompileResult = runProcess(processBuilder);
		    
		    // check compile errors
		    errorCodeIndex = testCompileResult.indexOf("error code");
		    errorCode = Integer.parseInt(testCompileResult.substring(errorCodeIndex+13 ,errorCodeIndex+14));	    
		    if(errorCode == 1) {
		    	compileIsOk = false;
		    	testsResult.add(testCompileResult);
		    	response.setCompileIsOk(false);
	     
		  	  	// delete created folder
			    processBuilder.directory(new File(System.getProperty("user.home") + fs + "candidate_test_system_projects" + fs + "test_launch"));
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
	    String report="";
	  // run unit tests
		    for(String testClassName: testClassesNameList) {
			    if (isWindows) {
			    	processBuilder.command("cmd.exe", "/c", "java -jar" + " %USERPROFILE%" + fs + "candidate_test_system_projects" + fs + "lib" + fs
			    			+ "junit-platform-console-standalone-1.9.3.jar" + " --class-path" + " target" + fs + "classes;target" + fs + "test_classes" 
			    			+ " --select-class" + " com.cleverhire." + "java_project" + projectKey + "." + testClassName);
			    } 
			    else if (isLinux){
			    	processBuilder.command("sh", "-c", "/opt/jdk-17.0.6/bin/java -jar" + " ~" + fs + "candidate_test_system_projects" + fs + "lib" + fs
			    			+ "junit-platform-console-standalone-1.9.3.jar" + " --class-path" + " target"  + fs + "classes:target" + fs + "test_classes" 
			    			+ " --select-class" + " com.cleverhire." + "java_project" + projectKey + "." + testClassName);
			    }
			    report = runProcess(processBuilder);
			    testsResult.add(report);
		    }
		    response.setCompileIsOk(true);
	  // delete created folder
		    processBuilder.directory(new File(System.getProperty("user.home") + fs + "candidate_test_system_projects" + fs + "test_launch"));
		    if (isWindows) {
		    	processBuilder.command("cmd.exe", "/c", "rmdir /s /q " + projectKey);
		    } 
		    else if (isLinux){
		    	processBuilder.command("sh", "-c", "rm -rf " + projectKey);
		    }
		    runProcess(processBuilder);
	    }

        response.setReport(testsResult);
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
