async function runPipeline() {
    const subreddit = document.getElementById('subreddit').value.trim();
    const limit = document.getElementById('limit').value;
    const btn = document.getElementById('runBtn');
    const statusEl = document.getElementById('status');
    const resultsEl = document.getElementById('results');

    if (!subreddit || !/^[a-zA-Z0-9_]{1,21}$/.test(subreddit)) {
        statusEl.innerHTML = '<span style="color:#f85149">Subreddit inválido.</span>';
        return;
    }

    btn.disabled = true;
    resultsEl.innerHTML = '';
    statusEl.innerHTML = `
        <div class="status-running">
            <div class="spinner"></div>
            A processar r/${subreddit}... pode demorar alguns minutos.
        </div>`;

    const form = new FormData();
    form.append('subreddit', subreddit);
    form.append('limit', limit);

    try {
        const res = await fetch('/api/run', { method: 'POST', body: form });
        const data = await res.json();

        if (!res.ok) {
            statusEl.innerHTML = `<span style="color:#f85149">${data.error || 'Erro desconhecido.'}</span>`;
            return;
        }

        statusEl.innerHTML = '';
        resultsEl.innerHTML = data.map(item => {
            if (item.status === 'success') {
                return `
                    <div class="result-item">
                        <span class="badge badge-success">OK</span>
                        <div>
                            <div class="result-text">${escapeHtml(item.title)}</div>
                            <div class="result-meta">${item.duration}s de processamento</div>
                        </div>
                        <a class="download-btn" href="/api/videos/${item.video}" download>⬇ Download</a>
                    </div>`;
            } else {
                return `
                    <div class="result-item">
                        <span class="badge badge-failure">ERRO</span>
                        <div>
                            <div class="result-text">${escapeHtml(item.reason)}</div>
                            <div class="result-meta">estágio: ${item.stage}</div>
                        </div>
                    </div>`;
            }
        }).join('');

        loadVideos();
    } catch (err) {
        statusEl.innerHTML = `<span style="color:#f85149">Erro de ligação ao servidor.</span>`;
    } finally {
        btn.disabled = false;
    }
}

async function loadVideos() {
    const listEl = document.getElementById('videoList');
    try {
        const res = await fetch('/api/videos');
        const videos = await res.json();

        if (videos.length === 0) {
            listEl.innerHTML = '<p class="empty">Nenhum vídeo gerado ainda.</p>';
            return;
        }

        listEl.innerHTML = videos.map(name => `
            <div class="video-item">
                <span>${escapeHtml(name)}</span>
                <a class="download-btn" href="/api/videos/${name}" download>⬇ Download</a>
            </div>`).join('');
    } catch {
        listEl.innerHTML = '<p class="empty">Erro ao carregar vídeos.</p>';
    }
}

function escapeHtml(str) {
    return String(str)
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;');
}

loadVideos();
