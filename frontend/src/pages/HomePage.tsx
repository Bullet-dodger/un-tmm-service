import { Link } from 'react-router-dom';

export default function HomePage() {
  return (
    <div className="page home-page">
      <h1>UN TMM Service</h1>
      <p className="subtitle">
        Сервис для расчёта адиабатической температуры горения
        на основе термодинамических данных материалов
      </p>

      <div className="home-cards">
        <Link to="/materials" className="home-card">
          <h2>Материалы</h2>
          <p>
            Просмотр каталога химических материалов с термодинамическими
            коэффициентами и температурными интервалами
          </p>
        </Link>

        <Link to="/calculator" className="home-card">
          <h2>Калькулятор</h2>
          <p>
            Расчёт адиабатической температуры горения
            и энтальпии реакции по заданным реагентам и продуктам
          </p>
        </Link>
      </div>
    </div>
  );
}
