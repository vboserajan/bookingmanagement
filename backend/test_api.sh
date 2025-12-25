#!/bin/bash

echo "========================================="
echo "Booking Management System - API Testing"
echo "========================================="
echo ""

# Colors for output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
RED='\033[0;31m'
NC='\033[0m' # No Color

BASE_URL="http://localhost:8080"

echo -e "${BLUE}1. Testing Login as USER${NC}"
echo "-----------------------------------"
LOGIN_RESPONSE=$(curl -s -X POST ${BASE_URL}/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"user","password":"password123"}' \
  -c user_cookies.txt)
echo "$LOGIN_RESPONSE" | head -c 200
echo ""
echo ""

echo -e "${BLUE}2. Getting Current User${NC}"
echo "-----------------------------------"
curl -s ${BASE_URL}/api/auth/current-user -b user_cookies.txt
echo ""
echo ""

echo -e "${BLUE}3. Getting All Users${NC}"
echo "-----------------------------------"
curl -s ${BASE_URL}/api/users -b user_cookies.txt | head -c 300
echo "..."
echo ""
echo ""

echo -e "${BLUE}4. Getting All Tasks${NC}"
echo "-----------------------------------"
curl -s ${BASE_URL}/api/tasks -b user_cookies.txt | head -c 500
echo "..."
echo ""
echo ""

echo -e "${BLUE}5. Creating a New Task${NC}"
echo "-----------------------------------"
NEW_TASK=$(curl -s -X POST ${BASE_URL}/api/tasks \
  -H "Content-Type: application/json" \
  -b user_cookies.txt \
  -d '{
    "title": "Test Task via API",
    "description": "Testing the booking system API",
    "priority": "HIGH",
    "assignedUserId": 2,
    "scheduledDate": "2025-12-26T14:00:00"
  }')
echo "$NEW_TASK" | head -c 300
TASK_ID=$(echo "$NEW_TASK" | grep -o '"id":[0-9]*' | head -1 | cut -d':' -f2)
echo ""
echo ""

echo -e "${BLUE}6. Login as MANAGER${NC}"
echo "-----------------------------------"
MANAGER_LOGIN=$(curl -s -X POST ${BASE_URL}/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"manager","password":"password123"}' \
  -c manager_cookies.txt)
echo "$MANAGER_LOGIN" | head -c 200
echo ""
echo ""

echo -e "${BLUE}7. Approving Task ID: ${TASK_ID}${NC}"
echo "-----------------------------------"
if [ ! -z "$TASK_ID" ]; then
  APPROVE_RESPONSE=$(curl -s -X PUT ${BASE_URL}/api/tasks/${TASK_ID}/approve \
    -b manager_cookies.txt)
  echo "$APPROVE_RESPONSE" | head -c 300
  echo ""
else
  echo "No task ID found to approve"
fi
echo ""

echo -e "${BLUE}8. Getting Tasks by Status (APPROVED)${NC}"
echo "-----------------------------------"
curl -s "${BASE_URL}/api/tasks?status=APPROVED" -b manager_cookies.txt | head -c 400
echo "..."
echo ""
echo ""

echo -e "${BLUE}9. Exporting Tasks to CSV${NC}"
echo "-----------------------------------"
curl -s "${BASE_URL}/api/tasks/export/csv" -b manager_cookies.txt -o tasks_export.csv
if [ -f tasks_export.csv ]; then
  echo -e "${GREEN}✓ CSV exported successfully${NC}"
  echo "First 3 lines:"
  head -3 tasks_export.csv
else
  echo -e "${RED}✗ CSV export failed${NC}"
fi
echo ""
echo ""

echo -e "${BLUE}10. Logout${NC}"
echo "-----------------------------------"
curl -s -X POST ${BASE_URL}/api/auth/logout -b manager_cookies.txt
echo -e "${GREEN}✓ Logged out${NC}"
echo ""

echo "========================================="
echo -e "${GREEN}API Testing Complete!${NC}"
echo "========================================="
