job("Git Pull")
{
 description ("Pulling Code from Github")
scm {
github ('gauravmehta13/Jenkins-Automation-Using-Groovy','master')
}
configure { it / 'triggers' / 'com.cloudbees.jenkins.GitHubPushTrigger' / 'spec' }
steps{
shell('sudo cp * -v /task3 ')
}
}
job("K8s Deployment")
{
description ("Kubernetes deployment")
steps{
shell('''sudo /usr/local/bin/kubectl version 
if sudo ls /task3 | grep html
then
  if sudo /usr/local/bin/kubectl get pvc | grep html
  then
  echo "pvc for html already created"
  else
  sudo /usr/local/bin/kubectl create -f /task3/html-pvc.yaml 
  fi
  if sudo /usr/local/bin/kubectl get deploy | grep html-webserver
  then
    echo "already running"
  else
    sudo /usr/local/bin/kubectl create -f /task3/html-deply.yaml 
  fi
else
echo "no html code from developer to host"
fi
htmlpod=$(/usr/local/bin/sudo kubectl get pod -l app=html-webserver -o jsonpath="{.items[0].metadata.name}" )
sudo /usr/local/bin/kubectl cp /task3/*.html   $htmlpod:/usr/local/apache2/htdocs ''')
}
 triggers {
        upstream('Git Pull', 'SUCCESS')
    }
}
job("Monitoring")
{
description ("monitoring the website")
steps{
shell(''' status=$(curl -o /dev/null -sw "%{http_code}" http://192.168.99.105:30001/index.html)
if [[$status == 200 ]]
then
echo "running"
else
curl -u admin:1234 http://192.168.99.106:8080/job/kub4/build?token=mail
fi ''')
}
triggers {
        upstream('K8s Deployment', 'SUCCESS')
    }
}
job("Mail")
{
description ("Mailing the developer")
 authenticationToken('mail')

 publishers {
        mailer('vaishnaviaggarwal1401@gmail..com', true, true)
    }
triggers {
        upstream('Monitoring', 'SUCCESS')
    }
}
