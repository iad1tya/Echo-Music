#!/bin/bash

# GitHub Readiness Verification Script for Echo Music
# This script checks if the project is ready to be pushed to GitHub

echo "🔍 Checking Echo Music project for GitHub readiness..."

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Check for sensitive files
echo "📁 Checking for sensitive files..."

SENSITIVE_FILES=(
    "google-services.json"
    "local.properties"
    "keystore.properties"
    "*.keystore"
    "*.jks"
    ".env"
    "secrets.properties"
    "api_keys.properties"
)

FOUND_SENSITIVE=false
for pattern in "${SENSITIVE_FILES[@]}"; do
    if find . -name "$pattern" -not -name "*.template" -not -path "./build/*" -not -path "./.gradle/*" | grep -q .; then
        echo -e "${RED}❌ Found sensitive file: $pattern${NC}"
        FOUND_SENSITIVE=true
    fi
done

if [ "$FOUND_SENSITIVE" = false ]; then
    echo -e "${GREEN}✅ No sensitive files found${NC}"
else
    echo -e "${RED}❌ Sensitive files found! Please remove them before pushing to GitHub.${NC}"
    exit 1
fi

# Check for API keys in source code
echo "🔑 Checking for hardcoded API keys..."

API_KEY_PATTERNS=(
    "AIzaSy[A-Za-z0-9_-]{33}"
    "sk-[A-Za-z0-9]{48}"
    "pk_[A-Za-z0-9]{24}"
)

FOUND_API_KEYS=false
for pattern in "${API_KEY_PATTERNS[@]}"; do
    if grep -r "$pattern" --exclude-dir=build --exclude-dir=.gradle --exclude="*.template" --exclude="*.apk" --exclude="*.aab" . | grep -q .; then
        echo -e "${RED}❌ Found potential API key pattern: $pattern${NC}"
        FOUND_API_KEYS=true
    fi
done

if [ "$FOUND_API_KEYS" = false ]; then
    echo -e "${GREEN}✅ No hardcoded API keys found${NC}"
else
    echo -e "${RED}❌ Hardcoded API keys found! Please replace them with placeholders.${NC}"
    exit 1
fi

# Check for template files
echo "📄 Checking for template files..."

TEMPLATE_FILES=(
    "google-services.json.template"
    "keystore.properties.template"
    "local.properties.template"
)

MISSING_TEMPLATES=false
for template in "${TEMPLATE_FILES[@]}"; do
    if ! find . -name "$template" | grep -q .; then
        echo -e "${RED}❌ Missing template file: $template${NC}"
        MISSING_TEMPLATES=true
    fi
done

if [ "$MISSING_TEMPLATES" = false ]; then
    echo -e "${GREEN}✅ All required template files present${NC}"
else
    echo -e "${RED}❌ Missing template files! Please ensure all template files are present.${NC}"
    exit 1
fi

# Check .gitignore
echo "🚫 Checking .gitignore configuration..."

if [ -f ".gitignore" ]; then
    echo -e "${GREEN}✅ .gitignore file exists${NC}"
    
    # Check if sensitive patterns are in .gitignore
    if grep -q "google-services.json" .gitignore && grep -q "local.properties" .gitignore; then
        echo -e "${GREEN}✅ Sensitive files are properly ignored${NC}"
    else
        echo -e "${YELLOW}⚠️  .gitignore may not cover all sensitive files${NC}"
    fi
else
    echo -e "${RED}❌ .gitignore file missing!${NC}"
    exit 1
fi

# Check for setup documentation
echo "📚 Checking for setup documentation..."

if [ -f "SETUP_GUIDE.md" ] || [ -f "README.md" ]; then
    echo -e "${GREEN}✅ Setup documentation exists${NC}"
else
    echo -e "${YELLOW}⚠️  Consider adding setup documentation${NC}"
fi

# Check for placeholder values
echo "🔧 Checking for placeholder values..."

PLACEHOLDER_PATTERNS=(
    "YOUR_FIREBASE_API_KEY"
    "YOUR_GOOGLE_API_KEY"
    "YOUR_PROJECT_NUMBER"
    "your-firebase-project-id"
)

FOUND_PLACEHOLDERS=false
for pattern in "${PLACEHOLDER_PATTERNS[@]}"; do
    if grep -r "$pattern" --exclude-dir=build --exclude-dir=.gradle . | grep -q .; then
        echo -e "${GREEN}✅ Found placeholder: $pattern${NC}"
        FOUND_PLACEHOLDERS=true
    fi
done

if [ "$FOUND_PLACEHOLDERS" = true ]; then
    echo -e "${GREEN}✅ Placeholder values are properly configured${NC}"
else
    echo -e "${YELLOW}⚠️  No placeholder values found - ensure API keys are replaced with placeholders${NC}"
fi

echo ""
echo -e "${GREEN}🎉 Project appears to be GitHub-ready!${NC}"
echo ""
echo "📋 Next steps:"
echo "1. Review all changes"
echo "2. Test the build process with placeholder values"
echo "3. Update README.md with setup instructions"
echo "4. Commit and push to GitHub"
echo ""
echo "⚠️  Remember:"
echo "- Never commit real API keys or sensitive data"
echo "- Always use template files for configuration"
echo "- Test builds after replacing placeholders"
