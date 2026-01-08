#!/bin/bash
# Script to push code to GitHub
# Replace YOUR_USERNAME and YOUR_REPO_NAME with your actual values

echo "Setting up GitHub remote..."
echo ""
echo "Please provide:"
read -p "GitHub username: " USERNAME
read -p "Repository name: " REPO_NAME

git remote add origin https://github.com/${USERNAME}/${REPO_NAME}.git 2>/dev/null || git remote set-url origin https://github.com/${USERNAME}/${REPO_NAME}.git

echo ""
echo "Pushing to GitHub..."
git push -u origin main

echo ""
echo "Done! Your code has been pushed to GitHub."
echo "Repository URL: https://github.com/${USERNAME}/${REPO_NAME}"
