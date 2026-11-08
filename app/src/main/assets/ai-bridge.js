(function(){
const KEY='jarvis.ai.config.v1';
const defaults={provider:'groq',model:'openai/gpt-oss-120b',endpoint:'https://api.groq.com/openai/v1/chat/completions',apiKey:''};
function config(){try{return Object.assign({},defaults,JSON.parse(localStorage.getItem(KEY)||'{}'))}catch(e){return {...defaults}}}
function save(c){localStorage.setItem(KEY,JSON.stringify(c))}
function el(tag,attrs,text){const n=document.createElement(tag);Object.entries(attrs||{}).forEach(([k,v])=>n.setAttribute(k,v));if(text)n.textContent=text;return n}
function setup(){
 if(document.getElementById('aiSetupButton'))return;
 const head=document.querySelector('header');if(!head)return;
 const b=el('button',{id:'aiSetupButton',type:'button'},'⚙ API');b.style.cssText='border:1px solid #31dcff;background:#082b42;color:#dffaff;border-radius:20px;padding:9px 13px;font-weight:700';
 head.appendChild(b);
 const panel=el('div',{id:'aiSetupPanel'});panel.style.cssText='display:none;position:fixed;z-index:9999;left:0;right:0;top:0;height:var(--jarvis-visual-height,100dvh);background:#000b;align-items:flex-start;justify-content:center;overflow:hidden;overscroll-behavior:none;padding:12px 12px max(12px,env(safe-area-inset-bottom));box-sizing:border-box';
 const card=el('div');card.style.cssText='width:min(100%,480px);max-height:calc(var(--jarvis-visual-height,100dvh) - 24px);overflow-y:auto;overscroll-behavior:contain;margin:0 auto;flex:0 1 auto;position:relative;transform:none;animation:none;transition:none;scrollbar-width:thin;background:#071b2c;border:1px solid #31dcff;border-radius:18px;padding:20px;color:#eafaff;box-shadow:0 0 35px #12bfe855;-webkit-overflow-scrolling:touch';
 const title=el('h2',{},'JARVIS CLOUD AI');title.style.marginTop='0';card.appendChild(title);
 const note=el('p',{},'Free-tier AI use karne ke liye provider ki API key chahiye. Key sirf is phone ke app storage mein rahegi; ise kisi ke saath share na karein. Free limits provider badal sakta hai.');note.style.cssText='font-size:13px;color:#9cc6dc;line-height:1.5';card.appendChild(note);
 const provider=el('select',{id:'aiProvider'});provider.style.cssText='width:100%;padding:12px;margin:7px 0;background:#041624;color:white;border:1px solid #1a6486;border-radius:10px';
 [['groq','Groq (free developer tier)'],['openrouter','OpenRouter (free models)']].forEach(([v,t])=>provider.appendChild(el('option',{value:v},t)));card.appendChild(provider);
 const model=el('input',{id:'aiModel',placeholder:'Model ID'});model.style.cssText='width:100%;padding:12px;margin:7px 0;background:#041624;color:white;border:1px solid #1a6486;border-radius:10px';card.appendChild(model);
 const key=el('input',{id:'aiKey',type:'text',inputmode:'text',autocapitalize:'off',spellcheck:'false',placeholder:'Paste Groq / OpenRouter API key'});key.autocomplete='off';key.style.cssText=model.style.cssText+';user-select:text;-webkit-user-select:text;';card.appendChild(key);
 const pasteBtn=el('button',{type:'button'},'📋 Paste key');pasteBtn.style.cssText='width:100%;padding:11px;margin:3px 0 7px;border:1px solid #31dcff;border-radius:10px;background:#0a2b42;color:#dffaff;font-weight:700';card.appendChild(pasteBtn);
 pasteBtn.onclick=()=>{try{let clip=window.JarvisNative&&window.JarvisNative.getClipboardText?window.JarvisNative.getClipboardText():'';if(clip){key.value=String(clip).trim();key.dispatchEvent(new Event('input',{bubbles:true}));key.focus();if(window.add)window.add('Clipboard se API key paste ho gayi.');}else if(navigator.clipboard&&navigator.clipboard.readText){navigator.clipboard.readText().then(v=>{key.value=String(v||'').trim();key.dispatchEvent(new Event('input',{bubbles:true}));key.focus();}).catch(()=>{key.focus();if(window.add)window.add('Clipboard access nahi mila. Key box ko long-press karke Paste karein.');});}else{key.focus();if(window.add)window.add('Key box ko long-press karke Paste karein.');}}catch(e){key.focus();if(window.add)window.add('Paste nahi hua. Key box ko long-press karke Paste karein.');}};
 const help=el('p',{},'Groq recommended model: openai/gpt-oss-120b  |  OpenRouter: openrouter/free');help.style.cssText='font-size:11px;color:#83a9bf';card.appendChild(help);
 const row=el('div');row.style.cssText='display:flex;gap:8px;margin-top:12px;position:sticky;bottom:-20px;padding:12px 0 0;background:linear-gradient(180deg,#071b2cdd,#071b2c 35%);z-index:2';
 const saveBtn=el('button',{type:'button'},'Save AI settings');saveBtn.style.cssText='flex:1;padding:12px;border:0;border-radius:10px;background:#20c9e8;color:#03111d;font-weight:700';
 const close=el('button',{type:'button'},'Close');close.style.cssText='padding:12px;border:1px solid #28617d;border-radius:10px;background:#0a2438;color:white';
 row.append(saveBtn,close);card.appendChild(row);panel.appendChild(card);document.body.appendChild(panel);
 function load(){const c=config();provider.value=c.provider;model.value=c.model;key.value=c.apiKey;setEndpoint()}
 function setEndpoint(){model.placeholder=provider.value==='groq'?'llama-3.3-70b-versatile':'openrouter/free'}
 provider.onchange=setEndpoint;function syncVisualHeight(){const vv=window.visualViewport;const h=vv&&vv.height?vv.height:window.innerHeight;panel.style.setProperty('--jarvis-visual-height',Math.max(240,Math.round(h))+'px')}if(window.visualViewport){window.visualViewport.addEventListener('resize',syncVisualHeight);window.visualViewport.addEventListener('scroll',syncVisualHeight)}window.addEventListener('resize',syncVisualHeight);syncVisualHeight();[model,key].forEach(field=>field.addEventListener('focus',()=>{syncVisualHeight();setTimeout(()=>{const r=field.getBoundingClientRect(),v=window.visualViewport;if(v&&(r.bottom>v.height-90||r.top<8))field.scrollIntoView({block:'nearest',behavior:'auto'})},80)}));b.onclick=()=>{load();syncVisualHeight();panel.style.display='flex';card.scrollTop=0};close.onclick=()=>{panel.style.display='none';if(document.activeElement&&document.activeElement.blur)document.activeElement.blur()};panel.addEventListener('click',e=>{if(e.target===panel)panel.style.display='none'});
 saveBtn.onclick=()=>{const p=provider.value;const apiKey=key.value.trim();if(!apiKey){if(window.add)window.add('API key khaali hai. Pehle apni provider key paste karein.');key.focus();return;}save({provider:p,model:model.value.trim()||(p==='groq'?'openai/gpt-oss-120b':'openrouter/free'),endpoint:p==='groq'?'https://api.groq.com/openai/v1/chat/completions':'https://openrouter.ai/api/v1/chat/completions',apiKey});panel.style.display='none';if(window.add)window.add('Cloud AI settings saved on this phone. API key is stored in this app WebView storage.');};
}
let seq=0, pending={};
window.onJarvisAIResult=function(id,ok,payload){const p=pending[id];if(!p)return;delete pending[id];if(!ok){p.reject(new Error(payload||'AI request failed'));return}try{const data=JSON.parse(payload);const text=data.choices&&data.choices[0]&&data.choices[0].message&&data.choices[0].message.content;if(!text)throw new Error('AI response empty');p.resolve(text)}catch(e){p.reject(e)}};
window.jarvisAskCloud= function(prompt){
 const c=config();if(!c.apiKey)return Promise.reject(new Error('AI key set karne ke liye upar ⚙ AI dabayein.'));
 const history=Array.from(document.querySelectorAll('#chat .msg')).filter(n=>n.textContent&&n.textContent!=='AI सोच रहा है…').slice(-13,-1).map(n=>(n.classList.contains('user')?'User: ':'Jarvis: ')+n.textContent);
 const enriched=history.length?'Recent conversation (oldest first):\n'+history.join('\n')+'\n\nCurrent user message:\n'+prompt:prompt;
 if(!window.JarvisNative||!window.JarvisNative.askAI)return Promise.reject(new Error('Native AI network bridge abhi available nahi hai.'));
 return new Promise((resolve,reject)=>{const id=String(++seq);pending[id]={resolve,reject};window.JarvisNative.askAI(id,c.endpoint,c.model,c.apiKey,enriched);});
};
const oldAnswer=window.answer;
window.answer=async function(raw){
 const q=String(raw||'').trim();if(!q)return;
 const l=q.toLowerCase();
 // Run supported app-opening commands on Android directly, not through the AI model.
 const appCommands=[
  {re:/(whatsapp|व्हाट्सएप)/i,pkg:'com.whatsapp',name:'WhatsApp'},
  {re:/(youtube|यूट्यूब)/i,pkg:'com.google.android.youtube',name:'YouTube'},
  {re:/(instagram|इंस्टाग्राम)/i,pkg:'com.instagram.android',name:'Instagram'}
 ];
 const appCommand=appCommands.find(c=>c.re.test(q)&&/(खोल|open|launch)/i.test(q));
 if(appCommand){
  let result='NOT_AVAILABLE';
  try{if(window.JarvisNative&&window.JarvisNative.openApp)result=window.JarvisNative.openApp(appCommand.pkg)}catch(e){result='OPEN_FAILED'}
  const message=result==='OK'?appCommand.name+' खोल रहा हूँ।':result==='NOT_INSTALLED'?appCommand.name+' इस फोन में इंस्टॉल नहीं है।':'ऐप खोलने का Android कंट्रोल उपलब्ध नहीं है।';
  if(window.add)window.add(message);if(window.say)window.say(message);return;
 }
 const local=/^(समय|कितने बजे|time|तारीख|दिनांक|date|आज कौन|नमस्ते|हेलो|hello|hi\b|तुम कौन|तुम्हारा नाम|who are you|मदद|help|क्या कर)/i.test(l);
 if(local&&typeof oldAnswer==='function'){oldAnswer(q);return}
 if(typeof window.add==='function')window.add('AI सोच रहा है…');
 const box=document.getElementById('chat');
 try{const answer=await window.jarvisAskCloud(q);if(box&&box.lastElementChild&&box.lastElementChild.textContent==='AI सोच रहा है…')box.lastElementChild.remove();window.add(answer);if(window.say)window.say(answer)}
 catch(e){if(box&&box.lastElementChild&&box.lastElementChild.textContent==='AI सोच रहा है…')box.lastElementChild.remove();const msg=e.message||'Cloud AI error';window.add('Cloud AI: '+msg+'  |  Settings ke liye ⚙ AI dabayein.');}
};
if(document.readyState==='loading')document.addEventListener('DOMContentLoaded',setup);else setup();
})();