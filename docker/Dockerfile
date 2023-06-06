FROM ubuntu:20.04
EXPOSE 8083
WORKDIR /
RUN addgroup guestgroup
RUN adduser  --ingroup guestgroup --disabled-password guest
RUN mkdir -p /home/guest/candidate_test_system_projects/test_launch 
RUN chown -R guest:guestgroup /home/guest/candidate_test_system_projects 
RUN chmod -R u+w /home/guest/candidate_test_system_projects 
COPY lib.tar.gz /home/guest/candidate_test_system_projects/lib.tar.gz
COPY jdk-17.0.6_linux-x64_bin.tar.gz /opt/jdk-17.0.6_linux-x64_bin.tar.gz
COPY CandidateTestSystemExecModule-0.1.jar /opt/CandidateTestSystemExecModule-0.1.jar 
RUN tar -xvzf /home/guest/candidate_test_system_projects/lib.tar.gz -C /home/guest/candidate_test_system_projects
RUN tar -xvzf /opt/jdk-17.0.6_linux-x64_bin.tar.gz -C /opt
USER guest
ENTRYPOINT ["/opt/jdk-17.0.6/bin/java", "-jar", "/opt/CandidateTestSystemExecModule-0.1.jar"]