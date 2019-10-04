oc delete all -l template=battlefield-bszeti
oc process -f battlefield-template.yaml -p IMAGE='quay.io/bszeti/fuse-player:latest' -p PLAYER_NAME=p0 -p PLAYER_URLS=p1,p2 -oyaml | oc create -f -
oc process -f battlefield-template.yaml -p IMAGE='quay.io/bszeti/fuse-player:latest' -p PLAYER_NAME=p1 -p PLAYER_URLS=p0,p2 -oyaml | oc create -f -
oc process -f battlefield-template.yaml -p IMAGE='quay.io/bszeti/fuse-player:latest' -p PLAYER_NAME=p2 -p PLAYER_URLS=p0,p1 -oyaml | oc create -f -

# oc rsh p1
# curl http://p0/hit/me
