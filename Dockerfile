FROM ubuntu:latest
LABEL authors="dooques"

ENTRYPOINT ["top", "-b"]