import Keycloak from 'keycloak-js';

const keycloak = new Keycloak({
    "realm": "cardgame-services",
    "url": "http://localhost:8080",
    "clientId": "hold-em-app"
});

export default keycloak;