@echo off
git add -A
git commit -m %1
echo Message is %1
git push