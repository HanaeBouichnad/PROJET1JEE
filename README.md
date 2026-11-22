# Projet JEE : Architecture micro-services avec un chatbot IA

Application bancaire basée sur une **architecture micro-services** (Spring Boot et Spring Cloud), complétée par un **chatbot intelligent** (Spring AI et MCP) accessible en REST, sur Telegram, sur Discord et depuis un **front Angular**.

Réalisé par **Hanae Bouichnad**, dans le cadre du module JEE.

---

## Architecture

```
      Angular (:4200) / Telegram / Discord / Postman
                          |
                          v
              +-----------------------+
              |   GATEWAY-SERVICE     |  :8888   point d'entrée unique
              +-----------------------+
                 |        |         |
                 v        v         v
   +--------------+  +------------------+  +-----------------+
   | CUSTOMER-    |  | BANK-ACCOUNT-    |  | CHATBOT-        |
   | SERVICE      |<-| SERVICE          |  | SERVICE         |
   | :8081        |  | :8082            |  | :8083           |
   | clients      |  | comptes          |  | agent IA        |
   | MCP Server   |  | OpenFeign +      |  | (Spring AI,     |
   |              |  | Circuit Breaker  |  |  Groq / Qwen)   |
   |              |  | MCP Server       |  | MCP Client      |
   +--------------+  +------------------+  +-----------------+
          ^                   ^                 |       |
          |       MCP         |       MCP       |       |
          +-------------------+-----------------+       |
          |                   |                         |
          +---------+---------+-------------------------+
                    |   inscription des services
                    v
          +------------------------+
          |  DISCOVERY-SERVICE     |  :8761   annuaire Eureka
          +------------------------+
```

| Service | Port | Rôle |
|---|---|---|
| **discovery-service** | 8761 | Annuaire Eureka : chaque service s'y inscrit avec son nom |
| **gateway-service** | 8888 | Point d'entrée unique, routage statique et dynamique |
| **customer-service** | 8081 | Gestion des clients (CRUD), expose ses fonctionnalités en outils MCP |
| **bank-account-service** | 8082 | Gestion des comptes (CRUD), appelle customer-service via OpenFeign, Circuit Breaker Resilience4J, outils MCP |
| **chatbot-service** | 8083 | Agent IA (Spring AI + modèle Qwen via Groq) avec mémoire, client MCP, streaming, bots Telegram et Discord |
| **angular-front** | 4200 | Interface web : comptes, clients et assistant IA |

---

## Technologies

- **Java 17**, **Spring Boot 4**, **Spring Cloud**
- **Spring Web**, **Spring Data JPA**, **H2**, **Lombok**
- **Eureka** (Discovery Service), **Spring Cloud Gateway**
- **OpenFeign** (communication entre services), **Resilience4J** (Circuit Breaker)
- **Spring AI** avec **Groq** (modèle `qwen/qwen3.8-27b`, API compatible OpenAI), **MCP** (Model Context Protocol)
- **Telegram Bots**, **Discord** (JDA)
- **Springdoc OpenAPI** (Swagger UI)
- **Angular**, **Bootstrap**, **Bootstrap Icons**, **ngx-markdown**

---

## Organisation de chaque micro-service

Chaque service métier suit une architecture en couches :

```
controllers   -> endpoints REST
services      -> logique métier (+ outils MCP avec @McpTool)
repositories  -> accès aux données (Spring Data JPA)
entities      -> tables de la base H2
dtos          -> données échangées avec le client (Request / Response)
mappers       -> conversion Entité <-> DTO
feign         -> client OpenFeign (bank-account-service)
model         -> objet Customer reçu de customer-service (bank-account-service)
```

---

## 1. Customer-Service

Tous les clients :

<a href="screenShots/customerScreens/1-showCustomers.png"><img src="screenShots/customerScreens/1-showCustomers.png" alt="Liste des clients" width="350"></a>

Un client par son id :

<a href="screenShots/customerScreens/2-showCustomer.png"><img src="screenShots/customerScreens/2-showCustomer.png" alt="Client par id" width="350"></a>

---

## 2. Bank-Account-Service

Tous les comptes (Swagger UI) :

<a href="screenShots/bankAccountScreens/1-showBankAccounts.png"><img src="screenShots/bankAccountScreens/1-showBankAccounts.png" alt="Liste des comptes" width="600"></a>

Un compte par son id :

<a href="screenShots/bankAccountScreens/2-showBankAccount.png"><img src="screenShots/bankAccountScreens/2-showBankAccount.png" alt="Compte par id" width="600"></a>

### Communication entre services avec OpenFeign

Bank-Account-Service appelle Customer-Service pour récupérer les informations du propriétaire du compte :

<a href="screenShots/bankAccountScreens/3-openFeign.png"><img src="screenShots/bankAccountScreens/3-openFeign.png" alt="OpenFeign" width="600"></a>

### Circuit Breaker (Resilience4J)

Quand Customer-Service est arrêté, Bank-Account-Service ne plante pas : la méthode de secours (fallback) renvoie un client par défaut.

<a href="screenShots/bankAccountScreens/4-resilience4j.png"><img src="screenShots/bankAccountScreens/4-resilience4j.png" alt="Circuit Breaker" width="600"></a>

---

## 3. Discovery Service (Eureka)

Les services inscrits dans l'annuaire :

<a href="screenShots/discoveryScreens/1-eureka.png"><img src="screenShots/discoveryScreens/1-eureka.png" alt="Eureka" width="600"></a>

---

## 4. Gateway

**Routage statique** (routes définies dans `application.yml`) :

<a href="screenShots/gatewayScreens/1-gatewayCustomers.png"><img src="screenShots/gatewayScreens/1-gatewayCustomers.png" alt="Gateway clients" width="350"></a>

<a href="screenShots/gatewayScreens/2-gatewayBankAccounts.png"><img src="screenShots/gatewayScreens/2-gatewayBankAccounts.png" alt="Gateway comptes" width="600"></a>

**Routage dynamique** (routes générées à partir d'Eureka, avec le nom du service dans l'URL) :

<a href="screenShots/gatewayScreens/3-gatewayChatbot1.png"><img src="screenShots/gatewayScreens/3-gatewayChatbot1.png" alt="Gateway dynamique vers le chatbot 1" width="600"></a>

<a href="screenShots/gatewayScreens/4-gatewayChatbot2.png"><img src="screenShots/gatewayScreens/4-gatewayChatbot2.png" alt="Gateway dynamique vers le chatbot 2" width="600"></a>

La Gateway autorise aussi les appels du front Angular grâce à une configuration **CORS** globale (`globalcors` dans `application.yml`).

---

## 5. Chatbot IA (Spring AI + MCP)

Les micro-services exposent leurs fonctionnalités comme **outils MCP** (`@McpTool`). Le chatbot les découvre et **le modèle choisit lui-même** l'outil à appeler selon la question.

Le modèle est servi par **Groq**, qui expose une API compatible OpenAI : Spring AI utilise donc le même client OpenAI, avec une autre `base-url`. Deux endpoints sont disponibles :

- `/chat` : la réponse complète en une seule fois ;
- `/chatStream` : la réponse envoyée morceau par morceau (`Flux<String>`).

Question générale (sans outil) :

<a href="screenShots/chatbotScreens/1-chatbot.png"><img src="screenShots/chatbotScreens/1-chatbot.png" alt="Question générale" width="600"></a>

Mémoire de la conversation :

<a href="screenShots/chatbotScreens/2-chatbotMemoire.png"><img src="screenShots/chatbotScreens/2-chatbotMemoire.png" alt="Mémoire" width="600"></a>

Liste des clients :

<a href="screenShots/chatbotScreens/3-chatbotCustomers.png"><img src="screenShots/chatbotScreens/3-chatbotCustomers.png" alt="Clients" width="600"></a>

Un client :

<a href="screenShots/chatbotScreens/4-chatbotCustomerId.png"><img src="screenShots/chatbotScreens/4-chatbotCustomerId.png" alt="Client 1" width="600"></a>

Tous les comptes :

<a href="screenShots/chatbotScreens/5-chatbotBankAccounts.png"><img src="screenShots/chatbotScreens/5-chatbotBankAccounts.png" alt="Comptes" width="600"></a>

Détails d'un compte, avec son propriétaire :

<a href="screenShots/chatbotScreens/6-chatbotBankAccountId.png"><img src="screenShots/chatbotScreens/6-chatbotBankAccountId.png" alt="Détails compte" width="600"></a>

Comptes d'un client sous forme de tableau :

<a href="screenShots/chatbotScreens/7-chatbotCustomerBankAccounts.png"><img src="screenShots/chatbotScreens/7-chatbotCustomerBankAccounts.png" alt="Tableau" width="600"></a>

### Bot Telegram

<a href="screenShots/chatbotScreens/8-chatbotTelegram.png"><img src="screenShots/chatbotScreens/8-chatbotTelegram.png" alt="Telegram" width="600"></a>

### Bot Discord

<a href="screenShots/chatbotScreens/9-chatbotDiscord.png"><img src="screenShots/chatbotScreens/9-chatbotDiscord.png" alt="Discord" width="600"></a>

---

## 6. Front Angular

Le front appelle les micro-services **à travers la Gateway** (`http://localhost:8888/NOM-DU-SERVICE/...`). Chaque page gère les états de la requête (succès / erreur), et un **intercepteur HTTP** affiche un indicateur de chargement pendant les appels.

Liste des comptes bancaires :

<a href="screenShots/angularScreens/1-accounts.png"><img src="screenShots/angularScreens/1-accounts.png" alt="Angular comptes" width="600"></a>

Liste des clients :

<a href="screenShots/angularScreens/2-customers.png"><img src="screenShots/angularScreens/2-customers.png" alt="Angular clients" width="600"></a>

### Assistant IA

La réponse du chatbot s'affiche **en streaming** (au fur et à mesure qu'elle est générée) et est mise en forme avec **ngx-markdown** (tableaux, listes, gras).

<a href="screenShots/angularScreens/3-bot1.png"><img src="screenShots/angularScreens/3-bot1.png" alt="Assistant IA 1" width="600"></a>

<a href="screenShots/angularScreens/4-bot2.png"><img src="screenShots/angularScreens/4-bot2.png" alt="Assistant IA 2" width="600"></a>

Ajout d'un compte bancaire au client 1 depuis le chatbot (outil MCP), avant et après :

<a href="screenShots/angularScreens/5-bot-avant-ajout-compteBancaire-client1.png"><img src="screenShots/angularScreens/5-bot-avant-ajout-compteBancaire-client1.png" alt="Avant ajout" width="600"></a>

<a href="screenShots/angularScreens/6-bot-apres-ajout-compteBancaire-client1.png"><img src="screenShots/angularScreens/6-bot-apres-ajout-compteBancaire-client1.png" alt="Après ajout" width="600"></a>

---

## Lancer le projet

### Variables d'environnement

Les clés ne sont pas dans le code. Avant de lancer `chatbot-service`, définir :

| Variable | Description |
|---|---|
| `GROQ_API_KEY` | clé de l'API Groq (https://console.groq.com) |
| `TELEGRAM_BOT_TOKEN` | token du bot Telegram (BotFather) |
| `DISCORD_TOKEN` | token du bot Discord |

### Ordre de démarrage

1. `discovery-service`
2. `customer-service`
3. `bank-account-service`
4. `gateway-service`
5. `chatbot-service` (après les services métier : il se connecte aux serveurs MCP au démarrage)
6. `angular-front` :

```
cd angular-front
npm install --legacy-peer-deps
ng serve
```

### URLs utiles

| URL | Description |
|---|---|
| http://localhost:4200 | Front Angular |
| http://localhost:8761 | Tableau de bord Eureka |
| http://localhost:8888/customers | Clients via la Gateway |
| http://localhost:8888/bankAccounts | Comptes via la Gateway |
| http://localhost:8083/chat?query=... | Chatbot (réponse complète) |
| http://localhost:8083/chatStream?query=... | Chatbot (streaming) |
| http://localhost:8083/swagger-ui.html | Swagger du chatbot |