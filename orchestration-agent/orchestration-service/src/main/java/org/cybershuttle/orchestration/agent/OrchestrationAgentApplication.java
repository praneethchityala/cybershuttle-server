package org.cybershuttle.orchestration.agent;

import com.hashicorp.nomad.apimodel.Job;
import com.hashicorp.nomad.javasdk.NomadApiClient;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.FileReader;
import java.io.IOException;

import static org.cybershuttle.orchestration.agent.service.NomadController.*;

@SpringBootApplication
public class OrchestrationAgentApplication {

	public static void main(String[] args) throws IOException, ParseException, InterruptedException {

		SpringApplication.run(OrchestrationAgentApplication.class, args);

//		JSONParser jsonParser = new JSONParser();
//
//		String file = "./myjob.json";
//
//		String job = ((JSONObject) jsonParser.parse(new FileReader(file))).toJSONString();
//
//		Job newJob = Job.fromJson(job);
//		NomadApiClient myClient = createConnection("http://localhost:4646");
//		Boolean startedJob = startJob(newJob,myClient);
//
//		System.out.println(startedJob);
//
//		Thread.sleep(10000);
//
//		Job runningJob = getJob(newJob.getId(), myClient);
//
//		System.out.println(runningJob.getId());
//
//		System.out.println(runningJob.getStatus());
//
//		Thread.sleep(10000);
//
//		Boolean killedJob = killJob(newJob,myClient);
//
//		System.out.println(killedJob);
//
//		runningJob = getJob(newJob.getId(), myClient);
//
//		System.out.println(runningJob.getId());
//
//		System.out.println(runningJob.getStatus());
	}

}
