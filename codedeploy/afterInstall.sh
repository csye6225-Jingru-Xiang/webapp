#!/bin/bash
sudo systemctl stop app
sudo chmod +x webservice-0.0.1-SNAPSHOT.jar

sudo /opt/aws/amazon-cloudwatch-agent/bin/amazon-cloudwatch-agent-ctl \
    -a fetch-config \
    -m ec2 \
    -c file:/opt/cloudwatch-config.json \
    -s

# cleanup log file
sudo rm -rf /var/log/niginx/*.log