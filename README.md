# UscoDemoStock

## Desarrollo

Para iniciar su aplicación en el perfil de desarrollo, ejecute:

```
./mvnw
```

## Producción

### Packaging como jar

Para construir el frasco final y optimizar la aplicación de servicio para producción, ejecute:

```
./mvnw -Pprod clean verify
```

Para asegurarse de que todo funcionó, ejecute:

```
java -jar target/*.jar
```

### Packaging como war

Para empaquetar su aplicación como war para implementarla en un servidor de aplicaciones, ejecute:

```
./mvnw -Pprod,war clean verify
```

## Usando Docker para simplificar el desarrollo (optional)

Then run:

```
docker-compose -f src/main/docker/app.yml up -d
```
