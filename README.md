# DevOps Project - CI/CD Pipeline & Monitoring

## Overview
This DevOps project demonstrates a complete CI/CD pipeline setup for a Java Spring Boot application using industry-standard tools. It includes automation from code commit to deployment, along with code quality checks, testing, containerization, artifact management, monitoring, and continuous delivery.

## 🔧 Technologies Used

- **Spring Boot**: Backend application framework
- **JUnit & Mockito**: Unit testing frameworks
- **Jenkins**: Continuous Integration and automation server
- **Docker & Docker Compose**: Containerization and orchestration
- **SonarQube**: Code quality and static code analysis
- **Nexus**: Artifact repository
- **Prometheus**: Metrics collection and monitoring
- **Grafana**: Visualization of metrics
- **Harness**: Continuous Delivery and GitOps platform
- **GitHub**: Source code repository

---

## 📦 Project Architecture

```
DevOps Pipeline
│
├── Code (GitHub)
│
├── Jenkins (CI)
│   └── Clones repo, installs dependencies, runs unit tests, SonarQube analysis, builds app, creates Docker image
│
├── SonarQube
│   └── Analyzes code quality
│
├── Docker
│   └── Builds containers for Spring Boot, MySQL, SonarQube, Nexus, Prometheus, Grafana
│
├── Nexus
│   └── Stores build artifacts (e.g. JAR files, Docker images)
│
├── Prometheus
│   └── Scrapes metrics from Spring Boot and MySQL using Micrometer
│
├── Grafana
│   └── Visualizes metrics using prebuilt dashboards (e.g., Dashboard ID 12900 for Spring Boot)
│
├── Harness (CD)
│   └── Deploys builds to environments using GitOps, connects to Jenkins
```

---

## 🛠️ Jenkins Pipeline Steps

```groovy
pipeline {
  agent any

  environment {
    SONARQUBE = credentials('sonarqube-token')
  }

  stages {
    stage('Checkout Code') {
      steps {
        git 'https://github.com/your-repo.git'
      }
    }

    stage('Install Dependencies') {
      steps {
        sh 'mvn clean install'
      }
    }

    stage('Run Unit Tests') {
      steps {
        sh 'mvn test'
      }
    }

    stage('SonarQube Analysis') {
      steps {
        withSonarQubeEnv('sonarqube') {
          sh 'mvn sonar:sonar'
        }
      }
    }

    stage('Build Application') {
      steps {
        sh 'mvn package'
      }
    }

    stage('Docker Build & Push') {
      steps {
        sh 'docker build -t your-docker-repo/app:latest .'
        sh 'docker push your-docker-repo/app:latest'
      }
    }

    stage('Deploy with Harness') {
      steps {
        echo 'Triggered Harness deployment'
      }
    }
  }
}
```

---

## ✅ Unit Testing
- **JUnit**: Tests business logic and core services
- **Mockito**: Mocks dependencies in isolation

### Example:
```java
@Test
void testServiceReturnsCorrectValue() {
  when(mockRepo.findById(1L)).thenReturn(Optional.of(new Item("Test")));
  Item item = service.getItem(1L);
  assertEquals("Test", item.getName());
}
```

---

## 📊 Monitoring with Prometheus & Grafana

### Spring Boot Configuration (application.properties):
```properties
management.endpoints.web.exposure.include=*
management.endpoint.prometheus.enabled=true
management.metrics.export.prometheus.enabled=true
```

### Prometheus Configuration:
```yaml
- job_name: 'spring-app'
  metrics_path: '/actuator/prometheus'
  static_configs:
    - targets: ['spring-boot-app:8080']

- job_name: 'mysql'
  static_configs:
    - targets: ['mysql-exporter:9104']
```

### Grafana:
- Connect to Prometheus as datasource
- Use dashboard ID **12900** for Spring Boot

---

## 🚀 Harness Deployment
- **Connected to Jenkins** as CI trigger
- Pulls build artifacts and images
- Deploys to Kubernetes or Docker-based environments
- Supports GitOps with rollback and approval steps

---

## 🔁 Docker Compose Services
```yaml
services:
  spring-app:
    build: .
    ports:
      - "8080:8080"
    networks:
      - my_network

  mysql:
    image: mysql:5.7
    environment:
      MYSQL_ROOT_PASSWORD: root
    networks:
      - my_network

  sonarqube:
    image: sonarqube
    ports:
      - "9000:9000"
    networks:
      - my_network

  prometheus:
    image: prom/prometheus
    volumes:
      - ./prometheus.yml:/etc/prometheus/prometheus.yml
    ports:
      - "9090:9090"
    networks:
      - my_network

  grafana:
    image: grafana/grafana
    ports:
      - "3000:3000"
    networks:
      - my_network

  nexus:
    image: sonatype/nexus3
    ports:
      - "8081:8081"
    networks:
      - my_network

networks:
  my_network:
    driver: bridge
```

---

## 📌 Final Notes
- This project simulates a real-world CI/CD and monitoring setup.
- Unit tests ensure code quality.
- Jenkins and SonarQube automate quality gates.
- Prometheus & Grafana help maintain operational health.
- Harness automates delivery to production.

> 🔐 Make sure to store credentials securely using Jenkins credentials store and `.env` files for Docker.

