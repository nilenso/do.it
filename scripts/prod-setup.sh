apt-get update
apt-get install -y nginx git openjdk-9-jdk postgresql
sudo -u postgres createuser enso -P
usersadd enso
createdb doit -U enso
# set the postgres password of enso user
# put .doit-secrets.edn file at /home/enso
# place doit.service at /etc/systemd/system/
# configure nginx; see nginx.conf.dev for reference
# Add your public key to /home/enso/.ssh/authorized_keys
