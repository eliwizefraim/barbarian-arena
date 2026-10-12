import { createClient } from '@supabase/supabase-js';

const supabaseUrl = 'WEKA_API_URL_YAKO_HAPA';
const supabaseKey = 'WEKA_PUBLISHABLE_KEY_YAKO_HAPA';

export const supabase = createClient(
  supabaseUrl,
  supabaseKey
);
