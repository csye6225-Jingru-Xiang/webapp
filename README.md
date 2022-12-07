# webapp.

new check for workflow

### Student
Name: Jingru Xiang 

NUID: 001586653

### Prerequisites
* Java 11
* Maven 3.8.6
* SpringBoot
* Postman
* Lombok Plugin
* MySql 8.0.28
* Navicat for MYSQL
* Packer

### Packer
1. packer init ./ami
2. packer fmt ./ami
3. packer validate ./ami
4. export AWS_ACCESS_KEY_ID="{YOUR_AWS_ACCESS_KEY_ID}"
5. export AWS_SECRET_ACCESS_KEY="{YOUR_ AWS_SECRET_ACCESS_KEY}"
6. packer build ./ami/aws-ubuntu.pkr.hcl

### Build Instructions
1. In terminal, use git clone + ssh to clone project
2. Open project in Intellij IDEA and use Maven to reload all the properties, use Maven package to pack .jar file under /target
3. Use Packer following steps above to build AMI
4. Use CloudFormation to start EC2 instance
5. Use SSH to connect to EC2 Instance and use port 3306 to connect with database client
6. Hit the three APIs using Postman on port 8080:
   * POST /v1/user 
   * GET /v1/user/{accountId}
   * PUT /v1/user/{accountId}
   * POST /v1/documents
   * GET /v1/documents
   * GET /v1/documents/{docId}
   * DELETE /v1/documents/{docId}
   

