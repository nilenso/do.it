latest_jar () {
    ls -tr target/*standalone.jar | tail -1
}

USER="enso"
HOST="doit.nilenso.com"
SCRIPT_CLJ="sudo service doit restart"

echo "-> Deploying Backend"
echo "-> Cleaning previous build"
lein clean


echo "-> Generating jar"
lein uberjar

echo "-> Deploying artifacts to server"
mv $(latest_jar) target/doit.jar
scp target/doit.jar ${USER}@${HOST}:doit/
ssh -l ${USER} -t ${HOST} "${SCRIPT_CLJ}"

echo "Backend Deployment Complete!"

# ----------------

SCRIPT_CLJS="
sudo rm -rf /var/www/*
sudo mv doit/www/* /var/www/
"
echo "-> Deploying Frontend"
echo "-> Cleaning previous build"
lein clean

echo "-> Compiling application"
lein build

echo "-> Deploying artifacts to server"
scp -r resources/public/* ${USER}@${HOST}:doit/www/
ssh -l ${USER} -t ${HOST} "${SCRIPT_CLJS}"

echo "-> Frontend deployment complete"
