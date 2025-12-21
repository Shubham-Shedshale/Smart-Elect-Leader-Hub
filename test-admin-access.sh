#!/bin/bash

echo "=== Electronic Voting System - Admin Access Test ==="
echo

# Step 1: Get login page to establish session
echo "1. Getting login page..."
curl -s -c cookies.txt http://localhost:8888/login > /dev/null

# Step 2: Login via API with session
echo "2. Logging in via API..."
LOGIN_RESPONSE=$(curl -s -X POST \
  -H "Content-Type: application/json" \
  -b cookies.txt \
  -c cookies.txt \
  -d '{"email":"admin@evoting.local","password":"Admin123!"}' \
  http://localhost:8888/api/auth/admin-login)

echo "Login response: $LOGIN_RESPONSE"

# Step 3: Access admin dashboard with session
echo "3. Accessing admin dashboard..."
DASHBOARD_RESPONSE=$(curl -s -b cookies.txt -L http://localhost:8888/admin/dashboard)

if echo "$DASHBOARD_RESPONSE" | grep -q "Admin Dashboard"; then
    echo "✅ SUCCESS: Admin dashboard accessible!"
    echo "📊 Dashboard content preview:"
    echo "$DASHBOARD_RESPONSE" | grep -o '<title>.*</title>' | head -1
else
    echo "❌ FAILED: Cannot access admin dashboard"
    echo "Response preview:"
    echo "$DASHBOARD_RESPONSE" | head -10
fi

# Cleanup
rm -f cookies.txt

echo
echo "=== Test Complete ==="