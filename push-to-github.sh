#!/bin/bash
# Script to push code to GitHub
# Usage: ./push-to-github.sh <username> <repo-name>

if [ $# -ne 2 ]; then
    echo "Usage: ./push-to-github.sh <github-username> <repository-name>"
    echo ""
    echo "Example: ./push-to-github.sh johndoe trading-system"
    exit 1
fi

USERNAME=$1
REPO_NAME=$2

echo "Setting up GitHub remote..."
git remote add origin https://github.com/${USERNAME}/${REPO_NAME}.git 2>/dev/null || git remote set-url origin https://github.com/${USERNAME}/${REPO_NAME}.git

echo ""
echo "Pushing to GitHub..."
git push -u origin main

if [ $? -eq 0 ]; then
    echo ""
    echo "✅ Success! Your code has been pushed to GitHub."
    echo "Repository URL: https://github.com/${USERNAME}/${REPO_NAME}"
else
    echo ""
    echo "❌ Push failed. Please check:"
    echo "   1. Repository exists on GitHub: https://github.com/${USERNAME}/${REPO_NAME}"
    echo "   2. You have push access to the repository"
    echo "   3. You're authenticated with GitHub (git credentials or SSH key)"
    exit 1
fi
