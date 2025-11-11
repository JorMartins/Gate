from flask import Flask, jsonify, render_template, request, redirect, url_for, session
from ldap3 import Server, Connection, ALL
import requests

app = Flask(__name__)
app.secret_key = 'BM0941_BM_Secret'

def validar_login(usuario, senha):
    try:
        domain = "DOMAIN"
        dc_ip = "10.1.1.17"

        if "\\" not in usuario:
            usuario = f"{domain}\\{usuario}"

        usuario_sem_dominio = usuario.split("\\")[-1]
        print(f"Tentando autenticar: {domain}\\{usuario_sem_dominio}")

        server = Server(dc_ip, get_info=ALL)
        conn = Connection(server, user=f"{domain}\\{usuario_sem_dominio}", password=senha, auto_bind=True)

        print("Autenticação bem-sucedida!")
        conn.unbind()
        return True

    except Exception as e:
        print(f"Erro geral: {str(e)}")
        return False

# Rota proxy para verificar se usuário existe
@app.route('/api/usuarios/existe/<username>')
def usuario_existe(username):
    try:
        print(f"Consultando API externa para usuário: {username}")
        response = requests.get(f'http://10.1.2.33:8123/api/usuarios/existe/{username}')
        print(f"Status da API externa: {response.status_code}")
        print(f"Conteúdo da API externa: {response.text}")
        
        # A API retorna apenas "true" ou "false" como texto
        content = response.text.strip().lower()
        print(f"Conteúdo processado: {content}")
        
        # Converte para boolean
        usuario_existe = content == 'true'
        
        return jsonify({'existe': usuario_existe}), response.status_code
    except Exception as e:
        print(f"Erro na rota proxy: {str(e)}")
        return jsonify({'error': str(e)}), 500

# Rota proxy para buscar tipo do usuário
@app.route('/api/usuarios/nome/<username>')
def usuario_tipo(username):
    try:
        print(f"Consultando tipo do usuário: {username}")
        response = requests.get(f'http://10.1.2.33:8123/api/usuarios/nome/{username}')
        print(f"Status da API tipo: {response.status_code}")
        print(f"Conteúdo da API tipo: {response.text}")
        
        # A API de tipo provavelmente retorna um JSON
        if response.text.strip():
            data = response.json()
            return jsonify(data), response.status_code
        else:
            return jsonify({'tipo': 'ADMIN'}), 200
            
    except Exception as e:
        print(f"Erro na rota proxy tipo: {str(e)}")
        return jsonify({'error': str(e)}), 500

@app.route('/')
def login():
    if session.get('logged_in'):
        return redirect_to_user_page(session.get('username'))
    return render_template('login.html')


def redirect_to_user_page(username):
    try:
        tipo_response = requests.get(f'http://10.1.2.33:8123/api/usuarios/nome/{username}')
        if tipo_response.ok:
            tipo_data = tipo_response.json()
            print(f"Dados do tipo recebidos: {tipo_data}")
            
            # Ajuste conforme a estrutura real da sua API
            user_type = tipo_data.get('tipo', 'ADMIN')
            user_type = user_type.upper() if user_type else 'ADMIN'
            print(f"Tipo determinado: {user_type}")
            
            if user_type == 'PORTARIA':
                return redirect(url_for('portaria'))
            elif user_type == 'LOGISTICA':
                return redirect(url_for('logistica'))
            elif user_type == 'FATURAMEN':  # NOVO: Adicionar FATURAMEN
                return redirect(url_for('faturament'))
            else:
                return redirect(url_for('admin'))
    except Exception as e:
        print(f"Erro ao buscar tipo do usuário: {e}")
    
    return redirect(url_for('admin'))



@app.route('/auth', methods=['POST'])
def auth():
    username = request.form['username']
    password = request.form['password']
    
    print(f"Tentativa de login para: {username}")
    
    # Verifica se usuário existe via nossa rota proxy
    try:
        existe_response = requests.get(f'http://127.0.0.1:5000/api/usuarios/existe/{username}')
        print(f"Status da verificação: {existe_response.status_code}")
        
        if existe_response.ok:
            existe_data = existe_response.json()
            print(f"Dados recebidos da verificação: {existe_data}")
            
            usuario_existe = existe_data.get('existe', False)
            print(f"Usuário existe: {usuario_existe}")
            
            if not usuario_existe:
                return render_template('login.html', error="Usuário não encontrado no sistema")
        else:
            return render_template('login.html', error="Erro ao verificar usuário")
            
    except Exception as e:
        print(f"Erro ao verificar usuário: {e}")
        return render_template('login.html', error="Erro ao conectar com o sistema")
    
    # Se usuário existe, valida no AD
    if validar_login(username, password):
        session['logged_in'] = True
        session['username'] = username.split('\\')[-1]
        print(f"Login AD bem-sucedido para: {session['username']}")
        return redirect_to_user_page(session['username'])
    else:
        return render_template('login.html', error="Credenciais inválidas")

@app.route('/portaria')
def portaria():
    if not session.get('logged_in'):
        return redirect(url_for('login'))
    username = session.get('username', 'Usuário desconhecido')
    return render_template('portaria.html', username=username)

@app.route('/logistica')
def logistica():
    if not session.get('logged_in'):
        return redirect(url_for('login'))   
    username = session.get('username', 'Usuário desconhecido')
    return render_template('logistica.html', username=username)

@app.route('/faturament')
def faturament():
    if not session.get('logged_in'):
        return redirect(url_for('login'))
    username = session.get('username', 'Usuário desconhecido')
    return render_template('faturament.html', username=username)

@app.route('/admin')
def admin():
    if not session.get('logged_in'):
        return redirect(url_for('login'))
    username = session.get('username', 'Usuário desconhecido')
    return render_template('admin.html', username=username)

@app.route('/logout')
def logout():
    session.clear()
    return redirect(url_for('login'))

@app.after_request
def after_request(response):
    response.headers.add('Access-Control-Allow-Origin', '*')
    response.headers.add('Access-Control-Allow-Headers', 'Content-Type,SOAPAction')
    response.headers.add('Access-Control-Allow-Methods', 'GET, POST, OPTIONS')
    return response












# Adicione estas rotas ao seu app.py
# Rotas para dispositivos - ajustadas para a estrutura da sua API
@app.route('/api/dispositivos', methods=['GET'])
def listar_dispositivos():
    try:
        response = requests.get('http://10.1.2.33:8123/api/dispositivos')
        return jsonify(response.json()), response.status_code
    except Exception as e:
        print(f"Erro ao listar dispositivos: {e}")
        return jsonify({'error': str(e)}), 500

@app.route('/api/dispositivos/<int:id>', methods=['PUT'])
def atualizar_dispositivo(id):
    try:
        data = request.get_json()
        # Ajuste o payload conforme necessário para sua API
        payload = {
            "descricao": data.get('descricao'),
            "imei": data.get('imei')
        }
        
        response = requests.put(
            f'http://10.1.2.33:8123/api/dispositivos/{id}',
            json=payload,
            headers={'Content-Type': 'application/json'}
        )
        return jsonify(response.json()), response.status_code
    except Exception as e:
        print(f"Erro ao atualizar dispositivo {id}: {e}")
        return jsonify({'error': str(e)}), 500

@app.route('/api/dispositivos/<int:id>', methods=['DELETE'])
def excluir_dispositivo(id):
    try:
        response = requests.delete(f'http://10.1.2.33:8123/api/dispositivos/{id}')
        return '', response.status_code
    except Exception as e:
        print(f"Erro ao excluir dispositivo {id}: {e}")
        return jsonify({'error': str(e)}), 500
    









# Rotas para Chats
# Rota para todos os chats ativos
@app.route('/api/chats/ativos', methods=['GET'])
def chats_ativos():
    try:
        response = requests.get('http://10.1.2.33:8123/api/chats/ativos')
        if response.ok:
            chats = response.json()
            
            # Enriquecer os dados com contagem de mensagens
            for chat in chats:
                chat['totalMensagens'] = len(chat.get('mensagens', []))
                # Também vamos adicionar o timestamp da última mensagem para facilitar
                if chat.get('mensagens'):
                    ultima_mensagem = max(chat['mensagens'], key=lambda x: x['data'])
                    chat['ultimaMensagemData'] = ultima_mensagem['data']
                else:
                    chat['ultimaMensagemData'] = chat['dataInicial']
            
            return jsonify(chats), response.status_code
        else:
            return jsonify({'error': 'Erro ao buscar chats'}), response.status_code
    except Exception as e:
        print(f"Erro ao buscar chats ativos: {e}")
        return jsonify({'error': str(e)}), 500


@app.route('/api/chats/<int:id_chat>/vincular-dispositivo/<int:id_dispositivo>', methods=['PUT'])
def vincular_dispositivo_chat(id_chat, id_dispositivo):
    try:
        response = requests.put(
            f'http://10.1.2.33:8123/api/chats/{id_chat}/vincular-dispositivo/{id_dispositivo}',
            headers={'Content-Type': 'application/json'}
        )
        return jsonify(response.json()), response.status_code
    except Exception as e:
        print(f"Erro ao vincular dispositivo ao chat: {e}")
        return jsonify({'error': str(e)}), 500

@app.route('/api/chats/<int:id_chat>/finalizar', methods=['PUT'])
def finalizar_chat(id_chat):
    try:
        response = requests.put(
            f'http://10.1.2.33:8123/api/chats/{id_chat}/finalizar',
            headers={'Content-Type': 'application/json'}
        )
        return jsonify(response.json()), response.status_code
    except Exception as e:
        print(f"Erro ao finalizar chat: {e}")
        return jsonify({'error': str(e)}), 500

# Adicione esta rota que está faltando
@app.route('/api/chats/ativos-sem-dispositivo', methods=['GET'])
def chats_ativos_sem_dispositivo():
    try:
        response = requests.get('http://10.1.2.33:8123/api/chats/ativos-sem-dispositivo')
        return jsonify(response.json()), response.status_code
    except Exception as e:
        print(f"Erro ao buscar chats ativos sem dispositivo: {e}")
        return jsonify({'error': str(e)}), 500









# Rotas para Chat Mensagens
# Rotas para Chat Mensagens
@app.route('/api/chat-mensagens/chat/<int:id_chat>', methods=['GET', 'POST'])
def chat_mensagens(id_chat):
    if request.method == 'GET':
        try:
            response = requests.get(f'http://10.1.2.33:8123/api/chat-mensagens/chat/{id_chat}')
            return jsonify(response.json()), response.status_code
        except Exception as e:
            print(f"Erro ao buscar mensagens do chat: {e}")
            return jsonify({'error': str(e)}), 500
    
    elif request.method == 'POST':
        try:
            # Alterado para PORTARIA
            operador = request.args.get('operador', 'PORTARIA')
            mensagem = request.args.get('mensagem', '')
            
            response = requests.post(
                f'http://10.1.2.33:8123/api/chat-mensagens/chat/{id_chat}',
                params={'operador': operador, 'mensagem': mensagem}
            )
            return jsonify(response.json()), response.status_code
        except Exception as e:
            print(f"Erro ao enviar mensagem: {e}")
            return jsonify({'error': str(e)}), 500





if __name__ == '__main__':
    app.run(debug=True, host='0.0.0.0', port=5000)