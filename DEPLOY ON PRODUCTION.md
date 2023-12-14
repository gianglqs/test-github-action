# Overview

This document provides step-by-step instructions for installing and deploying a React.js application using Yarn on an Ubuntu Server. Ensure you have access to a clean Ubuntu Server instance before proceeding.
Prerequisites

Before starting the installation process, make sure you have the following prerequisites:

    Ubuntu Server installed and accessible.
    Node.js and npm (Node Package Manager) installed.
    Yarn package manager installed.

## Steps

### Step 1: Update System Packages

#### Update the package index
`sudo apt update`

#### Upgrade installed packages
`sudo apt upgrade`

### Step 2: Install Node.js and npm
#### Install Node.js and npm using the package manager (Version 18 or higher)
`sudo apt install nodejs npm`

#### Verify the installation
```
node -v
npm -v
```

### Step 3:  Install Yarn

#### Add the Yarn repository key to your system
`curl -sS https://dl.yarnpkg.com/debian/pubkey.gpg | sudo apt-key add -`

#### Add the Yarn repository to your system
`echo "deb https://dl.yarnpkg.com/debian/ stable main" | sudo tee /etc/apt/sources.list.d/yarn.list`

#### Update the package index
`sudo apt update`

#### Install Yarn
`sudo apt install yarn`

#### Verify the installation
`yarn --version`

## Step 4: Clone the Spring Boot Application

### Install Git
`sudo apt install git`

### Update Github Deployment Key

#### Clone your frontend application repository
`git clone https://github.com/Phoenix-Software-Development/hyster-yale-frontend.git`

#### Navigate to the application directory
`cd /opt/hyster-yale-frontend`

## Step 5:  Install Application Dependencies using Yarn

### Build the React.js application
`yarn install | gnomon`  

## Step 6: Configure the backend configuration

Edit the `.env` change the `NEXT_PUBLIC_BACKEND_URL` to IP address of backend

## Step 7: Run the frontend application

`yarn build`

## Step 8: Access the Application

If the frontend has been deployed successfully, try to open a web browser and navigate to http://localhost:3000 to access your Spring Boot application.
