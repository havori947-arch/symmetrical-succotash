FROM python:3.12.3

WORKDIR /app

# Copy the distribution package
COPY dist/ds-service-1.0.tar.gz .

# Install the distribution package (MATCH THE FILE NAME!)
RUN pip install --no-cache-dir ds-service-1.0.tar.gz

# Set the environment variable for the Flask app
ENV FLASK_APP=src/app/__init__.py

# Expose the port
EXPOSE 8010

# Start the Flask app
CMD ["flask", "run", "--host=0.0.0.0", "--port=8010"]
