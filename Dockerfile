FROM ubuntu:latest
LABEL authors="Dmitry"

ENTRYPOINT ["top", "-b"]