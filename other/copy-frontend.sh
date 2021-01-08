sudo rm -rf /var/www/html/document-cloud/*
sudo cp -r /home/marko/IdeaProjects/DocumentCloud/frontend/* /var/www/html/document-cloud/
sudo systemctl reload nginx
tail -f /var/log/nginx/access.log
