#!/bin/bash

# Real Estate Platform Startup Script
echo "🏠 Starting Real Estate Platform..."

# Colors for output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Function to check if command exists
command_exists() {
    command -v "$1" >/dev/null 2>&1
}

# Check prerequisites
echo "📋 Checking prerequisites..."

if ! command_exists java; then
    echo -e "${RED}❌ Java not found. Please install Java 17 or higher.${NC}"
    exit 1
fi

if ! command_exists mvn; then
    echo -e "${RED}❌ Maven not found. Please install Maven.${NC}"
    exit 1
fi

if ! command_exists node; then
    echo -e "${RED}❌ Node.js not found. Please install Node.js 16 or higher.${NC}"
    exit 1
fi

if ! command_exists npm; then
    echo -e "${RED}❌ npm not found. Please install npm.${NC}"
    exit 1
fi

echo -e "${GREEN}✅ All prerequisites met!${NC}"

# Start PostgreSQL with Docker (optional)
if command_exists docker && command_exists docker-compose; then
    echo -e "\n${YELLOW}🐳 Starting PostgreSQL with Docker...${NC}"
    docker-compose up -d postgres
    echo "⏳ Waiting for PostgreSQL to be ready..."
    sleep 10
else
    echo -e "\n${YELLOW}⚠️  Docker not found. Make sure PostgreSQL is running manually.${NC}"
fi

# Start Backend
echo -e "\n${YELLOW}🚀 Starting Backend...${NC}"
cd backend
mvn spring-boot:run &
BACKEND_PID=$!
echo "Backend PID: $BACKEND_PID"
cd ..

# Wait for backend to start
echo "⏳ Waiting for backend to start..."
sleep 20

# Install frontend dependencies if needed
if [ ! -d "frontend/node_modules" ]; then
    echo -e "\n${YELLOW}📦 Installing frontend dependencies...${NC}"
    cd frontend
    npm install
    cd ..
fi

# Start Frontend
echo -e "\n${YELLOW}🎨 Starting Frontend...${NC}"
cd frontend
npm start &
FRONTEND_PID=$!
echo "Frontend PID: $FRONTEND_PID"
cd ..

echo -e "\n${GREEN}✅ Application started successfully!${NC}"
echo -e "${GREEN}📍 Frontend: http://localhost:3000${NC}"
echo -e "${GREEN}📍 Backend: http://localhost:8080${NC}"
echo -e "\n${YELLOW}Press Ctrl+C to stop all services${NC}"

# Wait for user interrupt
trap "echo -e '\n${YELLOW}Stopping services...${NC}'; kill $BACKEND_PID $FRONTEND_PID 2>/dev/null; exit 0" INT

wait

