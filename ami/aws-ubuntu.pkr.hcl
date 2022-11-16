packer {
  required_plugins {
    amazon = {
      version = ">= 1.1.1"
      source  = "github.com/hashicorp/amazon"
    }
  }
}

variable "aws_region" {
  type    = string
  default = "us-east-1"
}

variable "source_ami" {
  type    = string
  default = "ami-08c40ec9ead489470" # Ubuntu 22.04 LTS
}

variable "ssh_username" {
  type    = string
  default = "ubuntu"
}

source "amazon-ebs" "ubuntu" {
  region          = "${var.aws_region}"
  ami_name        = "csye6225_${formatdate("YYYY_MM_DD_hh_mm_ss", timestamp())}"
  ami_description = "AMI for CSYE 6225"
  ami_regions = [
    "us-east-1",
  ]

  aws_polling {
    delay_seconds = 120
    max_attempts  = 50
  }

  instance_type = "t2.micro"
  source_ami    = "${var.source_ami}"
  ssh_username  = "${var.ssh_username}"

  launch_block_device_mappings {
    delete_on_termination = true
    device_name           = "/dev/sda1"
    volume_size           = 8
    volume_type           = "gp2"
  }
}

build {
  sources = [
    "source.amazon-ebs.ubuntu"
  ]

  provisioner "shell" {
    inline = [
      "sudo apt-get update -y",
      "sudo apt-get install -y openjdk-11-jdk-headless",
      "sudo wget https://s3.amazonaws.com/amazoncloudwatch-agent/ubuntu/amd64/latest/amazon-cloudwatch-agent.deb",
      "sudo dpkg -i -E ./amazon-cloudwatch-agent.deb",
      "cd /var/log && sudo touch webapp.log",
      "sudo chmod 666 webapp.log"
    ]
  }

  provisioner "file" {
    source      = "./target/webapp-1.0-SNAPSHOT.jar"
    destination = "~/webapp-1.0-SNAPSHOT.jar"
  }

  provisioner "file" {
    source      = "./ami/cloudwatch.json"
    destination = "~/cloudwatch.json"
  }

  provisioner "shell" {
    scripts = [
      "./ami/runinstall.sh"
    ]
  }

  provisioner "shell" {
     scripts = [
       "./ami/codedeploy.sh"
     ]
   }
}
