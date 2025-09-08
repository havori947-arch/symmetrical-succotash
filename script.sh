#!/bin/bash
# setup.sh
# Script to install Docker, setup MySQL databases, and run services

set -e  # Exit immediately if a command fails

echo "🚀 Removing old Docker installations..."
for pkg in docker.io docker-doc docker-compose docker-compose-v2 podman-docker containerd runc; do 
  sudo apt-get remove -y $pkg || true
done

echo "🔑 Adding Docker's official GPG key..."
sudo apt-get update -y
sudo apt-get install -y ca-certificates curl
sudo install -m 0755 -d /etc/apt/keyrings
sudo curl -fsSL https://download.docker.com/linux/ubuntu/gpg -o /etc/apt/keyrings/docker.asc
sudo chmod a+r /etc/apt/keyrings/docker.asc

echo "📦 Adding Docker repository to Apt sources..."
echo \
  "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.asc] https://download.docker.com/linux/ubuntu \
  $(. /etc/os-release && echo "${UBUNTU_CODENAME:-$VERSION_CODENAME}") stable" | \
  sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
sudo apt-get update -y


echo "🐳 Installing Docker CE and plugins..."
sudo apt-get install -y docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin


echo "🐙 Installing standalone docker-compose..."
sudo apt-get install -y docker-compose

echo "✅ Docker installation complete!"

echo "🚀 Starting dependencies (dependencies-compose.yml)..."
sudo docker-compose -f dependencies-compose.yml up -d

echo "⏳ Waiting for MySQL to start..."
until sudo docker exec mysql mysqladmin ping -u root -pPASSWORD --silent &> /dev/null; do
  sleep 2
  echo "   ... MySQL not responding yet"
done

echo "🔐 Waiting for MySQL authentication to be ready..."
until sudo docker exec mysql mysql -u root -pPASSWORD -e "SELECT 1;" &> /dev/null; do
  sleep 2
  echo "   ... still waiting for MySQL authentication"
done



echo "📦 Creating databases in MySQL..."
sudo docker exec -i mysql mysql -u root -pPASSWORD <<EOF
CREATE DATABASE IF NOT EXISTS authservice;
CREATE DATABASE IF NOT EXISTS userservice;
CREATE DATABASE IF NOT EXISTS expenseservice;
EXIT;
EOF

echo "✅ Databases created successfully!"

echo "🚀 Starting main services (docker-compose.yml)..."
sudo docker-compose -f docker-compose.yml up -d

echo "🎉 Setup complete!"
