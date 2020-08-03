@echo off
IF [%1] == [] echo Please enter a commit message && EXIT /B ELSE 
git add -A
git commit -m %1
git push
