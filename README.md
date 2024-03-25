# PaymentChain Parent

## Main commands and urls

1. - Execute jar form terminal, passginspring boot profile, change the path, and change the profile by your desired \*

```shell
java -jar $HOME/.m2/repository/com/paymentchain/customer/0.0.1-SNAPSHOT/customer-0.0.1-SNAPSHOT.jar  --spring.profiles.active=prd
```

2. [Swagger interface](http://localhost:8081/swagger-ui/index.html)
3. [REST get method check profile on cutomer](http://localhost:8081/customer/check)
4. [Acces to h2 database](http://localhost:8081/h2-console/)
5. [REST get method config from spring config](http://localhost:8888/customer-dev/development)
6. [Admin services](http://localhost:8762/applications)
7. [PostgreSQL Client](http://localhost:80)

## Commands

```shell
docker-compose start -d
```

```shell
docker-compose stop
```

```shell
docker-compose up -d --force-recreate
```
