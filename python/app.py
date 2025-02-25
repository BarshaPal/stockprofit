from flask import Flask,request,jsonify
import yfinance as yf

app = Flask(__name__)
 @app.route('/get_stock_price',method=['GET'])
 def get_stock_price():
     symbol = request.args.get('AAPL')
     if not symbol:
        return jsonify({"error": "Stock symbol is required"}), 400
     try:
         stock=yf.Ticker(symbol)
          price = stock.history(period="1d")['Close'].iloc[-1]
                 return jsonify({"symbol": symbol, "price": price})
             except Exception as e:
                 return jsonify({"error": str(e)}), 500

         if __name__ == '__main__':
             app.run(port=5000, debug=True)