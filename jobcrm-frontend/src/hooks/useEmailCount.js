import { useState, useEffect } from 'react';
import { getEmails } from '../api';

export function useEmailCount() {
  const [draftCount, setDraftCount] = useState(0);

  useEffect(() => {
    const fetch = async () => {
      try {
        const res = await getEmails();
        const drafts = res.data.filter(e => e.status === 'DRAFT').length;
        setDraftCount(drafts);
      } catch (err) {
        console.error('Failed to fetch email count', err);
      }
    };
    fetch();
    const interval = setInterval(fetch, 30000);
    return () => clearInterval(interval);
  }, []);

  return draftCount;
}
