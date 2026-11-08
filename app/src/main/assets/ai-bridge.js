(function(){
const KEY='jarvis.ai.config.v1';
const defaults={provider:'groq',model:'openai/gpt-oss-120b',endpoint:'https://api.groq.com/openai/v1/chat/completions',apiKey:''};
function config(){try{return Object.assign({},defaults,JSON.parse(localStorage.getItem(KEY)||'{}'))}catch(e){return {...defaults}}}
function save(c){localStorage.setItem(KEY,JSON.stringify(c))}
function el(tag,attrs,text){const n=document.createElement(tag);Object.entries(attrs||{}).forEach(([k,v])=>n.setAttribute(k,v));if(text)n.textContent=text;return n}
function setup(){
 if(document.getElementById('aiSetupButton'))return;
 const head=document.querySelector('header');if(!head)return;
 const b=el('button',{id:'aiSetupButton',type:'button'},'⚙ AI');b.style.cssText='border:1px solid #31dcff;background:#082b42;color:#dffaff;border-radius:20px;padding:9px 13px;font-weight:700';
 head.appendChild(b);
 const panel=el('div',{id:'aiSetupPanel'});panel.style.cssText='display:none;position:fixed;z-index:9999;inset:0;background:#000b;align-items:center;justify-content:center;padding:18px';
 const card=el('div');card.style.cssText='width:min(100%,480px);max-height:90vh;overflow:auto;background:#071b2c;border:1px solid #31dcff;border-radius:18px;padding:20px;color:#eafaff;box-shadow:0 0 35px #12bfe855';
 const title=el('h2',{},'JARVIS CLOUD AI');title.style.marginTop='0';card.appendChild(title);
 const note=el('p',{},'Free-tier AI use karne ke liye provider ki API key chahiye. Key sirf is phone ke app storage mein rahegi; ise kisi ke saath share na karein. Free limits provider badal sakta hai.');note.style.cssText='font-size:13px;color:#9cc6dc;line-height:1.5';card.appendChild(note);
 const provider=el('select',{id:'aiProvider'});provider.style.cssText='width:100%;padding:12px;margin:7px 0;background:#041624;color:white;border:1px solid #1a6486;border-radius:10px';
 [['groq','Groq (free developer tier)'],['openrouter','OpenRouter (free models)']].forEach(([v,t])=>provider.appendChild(el('option',{value:v},t)));card.appendChild(provider);
 const model=el('input',{id:'aiModel',placeholder:'Model ID'});model.style.cssText='width:100%;padding:12px;margin:7px 0;background:#041624;color:white;border:1px solid #1a6486;border-radius:10px';card.appendChild(model);
 const key=el('input',{id:'aiKey',type:'password',placeholder:'API key'});key.autocomplete='off';key.style.cssText=model.style.cssText;card.appendChild(key);
 const help=el('p',{},'Groq recommended model: openai/gpt-oss-120b  |  OpenRouter: openrouter/free');help.style.cssText='font-size:11px;color:#83a9bf';card.appendChild(help);
 const row=el('div');row.style.cssText='display:flex;gap:8px;margin-top:12px';
 const saveBtn=el('button',{type:'button'},'Save AI settings');saveBtn.style.cssText='flex:1;padding:12px;border:0;border-radius:10px;background:#20c9e8;color:#03111d;font-weight:700';
 const close=el('button',{type:'button'},'Close');close.style.cssText='padding:12px;border:1px solid #28617d;border-radius:10px;background:#0a2438;color:white';
 row.append(saveBtn,close);card.appendChild(row);panel.appendChild(card);document.body.appendChild(panel);
 function load(){const c=config();provider.value=c.provider;model.value=c.model;key.value=c.apiKey;setEndpoint()}
 function setEndpoint(){model.placeholder=provider.value==='groq'?'llama-3.3-70b-versatile':'openrouter/free'}
 provider.onchange=setEndpoint;b.onclick=()=>{load();panel.style.display='flex'};close.onclick=()=>panel.style.display='none';panel.addEventListener('click',e=>{if(e.target===panel)panel.style.display='none'});
 saveBtn.onclick=()=>{const p=provider.value;save({provider:p,model:model.value.trim()||(p==='groq'?'openai/gpt-oss-120b':'openrouter/free'),endpoint:p==='groq'?'https://api.groq.com/openai/v1/chat/completions':'https://openrouter.ai/api/v1/chat/completions',apiKey:key.value.trim()});panel.style.display='none';if(window.add)window.add('Cloud AI settings saved on this phone.');};
}
let seq=0, pending={};
window.onJarvisAIResult=function(id,ok,payload){const p=pending[id];if(!p)return;delete pending[id];if(!ok){p.reject(new Error(payload||'AI request failed'));return}try{const data=JSON.parse(payload);const text=data.choices&&data.choices[0]&&data.choices[0].message&&data.choices[0].message.content;if(!text)throw new Error('AI response empty');p.resolve(text)}catch(e){p.reject(e)}};
window.jarvisAskCloud= function(prompt){
 const c=config();if(!c.apiKey)return Promise.reject(new Error('AI key set karne ke liye upar ⚙ AI dabayein.'));
 if(!window.JarvisNative||!window.JarvisNative.askAI)return Promise.reject(new Error('Native AI network bridge abhi available nahi hai.'));
 return new Promise((resolve,reject)=>{const id=String(++seq);pending[id]={resolve,reject};window.JarvisNative.askAI(id,c.endpoint,c.model,c.apiKey,prompt);});
};
const oldAnswer=window.answer;
window.answer=async function(raw){
 const q=String(raw||'').trim();if(!q)return;
 const l=q.toLowerCase();
 const local=/^(समय|कितने बजे|time|तारीख|दिनांक|date|आज कौन|नमस्ते|हेलो|hello|hi\b|तुम कौन|तुम्हारा नाम|who are you|मदद|help|क्या कर)/i.test(l);
 if(local&&typeof oldAnswer==='function'){oldAnswer(q);return}
 if(typeof window.add==='function')window.add('AI सोच रहा है…');
 const box=document.getElementById('chat');
 try{const answer=await window.jarvisAskCloud(q);if(box&&box.lastElementChild&&box.lastElementChild.textContent==='AI सोच रहा है…')box.lastElementChild.remove();window.add(answer);if(window.say)window.say(answer)}
 catch(e){if(box&&box.lastElementChild&&box.lastElementChild.textContent==='AI सोच रहा है…')box.lastElementChild.remove();const msg=e.message||'Cloud AI error';window.add('Cloud AI: '+msg+'  |  Settings ke liye ⚙ AI dabayein.');}
};
if(document.readyState==='loading')document.addEventListener('DOMContentLoaded',setup);else setup();
})();