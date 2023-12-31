import { useNavigate } from 'react-router-dom';

interface propsType {
  state: number;
}

// state 번호에 따라 선택 된 컴포넌트가 다름

const MyClosetBar = ({ state }: propsType) => {
  const navigate = useNavigate();

  return (
    <>
      <div className="flex gap-3 justify-center mt-4 mb-4">
        <div
          className={`w-[20%] h-[65px] max-w-[250px] min-w-[92px] tracking-wider shadow-md flex items-center justify-center rounded-2xl bg-gray-button ${
            state === 1 ? 'bg-pink-light' : 'bg-gray-button'
          } hover:bg-pink-light`}
          onClick={() => navigate('/mycloset')}>
          옷장
          <br />
          보기
        </div>
        <div
          className={`w-[20%] h-[65px] max-w-[250px] min-w-[92px] tracking-wider shadow-md flex items-center justify-center rounded-2xl bg-gray-button ${
            state === 2 ? 'bg-pink-light' : 'bg-gray-button'
          } hover:bg-pink-light`}
          onClick={() => navigate('/mycloset/add-cloth')}>
          옷장
          <br />
          등록
        </div>
        <div
          className={`w-[20%] h-[65px] max-w-[250px] min-w-[92px] tracking-wider shadow-md flex items-center justify-center rounded-2xl bg-gray-button ${
            state === 3 ? 'bg-pink-light' : 'bg-gray-button'
          } hover:bg-pink-light`}
          onClick={() => navigate('/mycloset/report')}>
          옷장
          <br />
          분석
        </div>
      </div>
    </>
  );
};

export default MyClosetBar;
