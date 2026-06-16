#!/bin/bash
# setup-env.sh - Safe credential loading script
# This script helps developers load credentials without exposing them

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${GREEN}=== Salesforce Test Automation Setup ===${NC}"
echo

# Check if .env file exists
if [ ! -f .env ]; then
    echo -e "${YELLOW}⚠️  .env file not found${NC}"
    echo "Creating .env from .env.example..."
    cp .env.example .env
    echo -e "${RED}❌ Please edit .env with your test credentials${NC}"
    echo "Then run this script again."
    exit 1
fi

# Verify .env is in .gitignore
if ! grep -q "^\.env$" .gitignore 2>/dev/null; then
    echo -e "${YELLOW}⚠️  Adding .env to .gitignore${NC}"
    echo ".env" >> .gitignore
fi

# Load credentials from .env
export $(cat .env | grep -v '^#' | xargs)

# Verify credentials are loaded
if [ -z "$SF_USERNAME" ]; then
    echo -e "${RED}❌ Error: SF_USERNAME not set in .env${NC}"
    exit 1
fi

echo -e "${GREEN}✓ Credentials loaded from .env${NC}"
echo -e "${GREEN}✓ SF_USERNAME: ${SF_USERNAME:0:10}...${NC}"
echo -e "${GREEN}✓ SF_PASSWORD: $(echo $SF_PASSWORD | head -c 3)...${NC}"
echo

# Check if running tests
if [ "$1" == "test" ]; then
    echo -e "${GREEN}Running tests with credentials...${NC}"
    echo "Command: mvn clean test -Dheadless=false -DvisualAgent=true -Dsf.reset.submit.enabled=true"
    echo
    mvn clean test -Dheadless=false -DvisualAgent=true -Dsf.reset.submit.enabled=true
    EXIT_CODE=$?

    if [ $EXIT_CODE -eq 0 ]; then
        echo -e "${GREEN}✓ Tests passed${NC}"
    else
        echo -e "${RED}❌ Tests failed${NC}"
    fi
    exit $EXIT_CODE
else
    echo -e "${GREEN}✓ Setup complete. Ready to run tests.${NC}"
    echo "Usage:"
    echo "  ./setup-env.sh test              # Run tests with credentials"
    echo "  source ./setup-env.sh            # Load credentials in current shell"
fi
