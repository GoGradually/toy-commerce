import {rmSync} from 'node:fs';
import {resolve} from 'node:path';
import {generate} from 'openapi-typescript-codegen';

const defaultSource = 'http://localhost:8080/v3/api-docs';
const input = process.env.OPENAPI_SOURCE ?? defaultSource;
const output = resolve(process.cwd(), 'src/shared/api/generated/client');

rmSync(output, {recursive: true, force: true});

await generate({
    input,
    output,
    httpClient: 'fetch',
    useOptions: true,
    useUnionTypes: true,
    exportCore: true,
    exportModels: true,
    exportServices: true
});

console.log(`Generated API client from ${input}`);
console.log(`Output directory: ${output}`);
