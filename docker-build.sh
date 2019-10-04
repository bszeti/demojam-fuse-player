mvn clean package -DskipTests
docker build -t quay.io/bszeti/battlefield-player-fuse:latest .
docker push quay.io/bszeti/battlefield-player-fuse:latest
