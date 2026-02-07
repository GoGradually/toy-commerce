import {OpenAPI} from './generated/client';
import {env} from '../config/env';

OpenAPI.BASE = env.apiBaseUrl;
