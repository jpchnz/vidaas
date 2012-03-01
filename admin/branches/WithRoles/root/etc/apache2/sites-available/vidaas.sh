cat <<EndOfFile
JkWorkerProperty worker.list=vidaas
JkWorkerProperty worker.vidaas.port=8009
<VirtualHost *:80>
    ServerAdmin webmaster@localhost
    #redirect docroot
    RewriteEngine on
    RewriteRule ^/$ /vidaas/ [R]
    #mount vidaas
    JkMount /* vidaas
    #logging
    ErrorLog \${APACHE_LOG_DIR}/error.log
    LogLevel warn
    CustomLog \${APACHE_LOG_DIR}/access.log combined
</VirtualHost>
EndOfFile
