import { Tracer, BatchRecorder, jsonEncoder } from 'zipkin';
import { HttpLogger } from 'zipkin-transport-http';
import CLSContext from 'zipkin-context-cls';
import { expressMiddleware } from 'zipkin-instrumentation-express';

// Default to localhost if not configured
const ZIPKIN_ENDPOINT = process.env.ZIPKIN_URL || 'http://zipkin:9411';
const SERVICE_NAME = 'test-node';

// Setup the tracer
const recorder = new BatchRecorder({
  logger: new HttpLogger({
    endpoint: `${ZIPKIN_ENDPOINT}/api/v2/spans`,
    jsonEncoder: jsonEncoder.JSON_V2
  })
});

const ctxImpl = new CLSContext('zipkin');
const tracer = new Tracer({ ctxImpl, recorder, localServiceName: SERVICE_NAME });

// Create middleware for Express
const zipkinMiddleware = expressMiddleware({ tracer });

export { tracer, zipkinMiddleware };
