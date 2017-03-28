#!/bin/bash
SERVER_DIR="/usr/share/apache-tomcat-8.5.8/"
WEBAPP_DIR="webapps/green-house-0.0.1-SNAPSHOT/"
PICTURES_DIR="content/images/plant_pictures/"
FINAL_DIR=$SERVER_DIR$WEBAPP_DIR$PICTURES_DIR

DATE=$(date +"%Y-%m-%d_%H%M")

mkdir -p $FINAL_DIR

raspistill -vf -hf -o $FINAL_DIR$DATE.jpg
jpegoptim -m 75  $FINAL_DIR$DATE.jpg
