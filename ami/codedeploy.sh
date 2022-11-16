#!/bin/bash
sudo apt-get install ruby -y
CODEDEPLOY_BIN="/opt/codedeploy-agent/bin/codedeploy-agent"
$CODEDEPLOY_BIN stop
apt-get remove codedeploy-agent -y
cd /home/ubuntu
wget https://aws-codedeploy-us-east-1.s3.us-east-1.amazonaws.com/latest/install
sudo chmod +x ./install
sudo ./install auto
echo $(sudo service codedeploy-agent status)