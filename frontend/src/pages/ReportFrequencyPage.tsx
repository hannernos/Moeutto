import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import Swal from 'sweetalert2';

import { authInstance, defaultInstance } from '../api/api';

import IntroComment from '../components/report/atoms/IntroComment';
import ShortReportComment from '../components/report/atoms/ShortReportComment';
import ReportComment from '../components/report/atoms/ReportComment';
import ReportFrequency from '../components/report/atoms/ReportFrequency';
import ReportFrequencyDonation from '../components/report/atoms/ReportFrequencyDonation';

interface frequencyItem {
  divColor: string;
  clothesImage: string;
  frequencyAmount: number;
}

const colorArray = ['#FFFFFF', '#FFFFFF', '#FFFFFF'];

let mostCount;
let leastCount;

const ReportFrequencyPage = () => {
  const naviagte = useNavigate();

  const goMainPage = () => {
    naviagte('/main');
  };

  const [myMostFrequency, setMyMostFrequency] = useState([]);
  const [myLeastFrequency, setMyLeastFrequency] = useState([]);
  const [mostFrequencyComment, setMostFrequencyComment] = useState('');
  const [leastFrequencyComment, setLeastFrequencyComment] = useState('');

  const fetchData = async () => {
    const axiosInstance = authInstance({ ContentType: 'application/json' });
    const response = await axiosInstance.get('/clothes/analysis-frequency');

    if (response.data.code === 3002) {
      Swal.fire({
        icon: 'error',
        html: '옷을 먼저 등록해주세요',
        showCancelButton: false,
        confirmButtonText: '확인',
        confirmButtonColor: '#FF78A5'
      });
      goMainPage();
    } else {
      mostCount = response.data.data.myMostFrequency[0].frequency;
      leastCount = response.data.data.myLeastFrequency[0].frequency;

      const most: frequencyItem[] = response.data.data.myMostFrequency.map((row, index) => ({
        divColor: colorArray[index],
        clothesImage: row.imageUrl,
        frequencyAmount: row.frequency,
      }));

      const least: frequencyItem[] = response.data.data.myLeastFrequency.map((row, index) => ({
        divColor: colorArray[index],
        clothesImage: row.imageUrl,
        frequencyAmount: row.frequency,
      }));

      setMyMostFrequency(most);
      setMyLeastFrequency(least);

      const axiosMostCategoryInstance = defaultInstance();
      const mostCategoryResponse = await axiosMostCategoryInstance.get(
        `/middle-categories/${response.data.data.myMostFrequency[0].middleCategoryId}`
      );

      const axiosLeastCategoryResponse = defaultInstance();
      const leastcategoryResponse = await axiosLeastCategoryResponse.get(
        `/middle-categories/${response.data.data.myLeastFrequency[0].middleCategoryId}`
      );

      const mostCategory = mostCategoryResponse.data.data.name;
      const leastCategory = leastcategoryResponse.data.data.name;

      // 현재 코멘트는 중분류 카테고리 id 로만 코멘트 줌
      setMostFrequencyComment(`${mostCategory} 을/를 자주 입으시는 군요`);

      setLeastFrequencyComment(
        `${leastCategory} 을/를 제일 적게 입으시는 군요\n혹시 입지 않으신다면 나눔을 하는 것은 어떨까요?`
      );
    }
  };

  useEffect(() => {
    fetchData();
  }, []);

  return (
    <>
      {/* 인트로 분석 문구 */}
      <IntroComment nickname={`${sessionStorage.getItem('nickname')}`} imageUrl="/images/report.png" />

      {/* 간단 분석 문구 */}
      <ShortReportComment
        imageDivClass="absolute top-[-35px] left-[10px]"
        imageUrl="/images/earth.png"
        imageClass="w-16 inline-block"
        mainTitle="당신은 지구지킴이?"
      />

      {/* 많이 입은 옷 */}
      <div className="mb-4">
        <ReportComment
          divPadding=""
          imageUrl="/images/report-happy.png"
          imageClass="w-20 inline-block"
          mainTitle="가장 많이 입는 옷은 무엇일까요?"
          subTitle={`가장 많이 입은 횟수는 무려 ${mostCount}회`}
        />
      </div>

      {/* 많이 입은 옷 분석 문구 */}
      <div className="flex flex-row items-center justify-center">
        <div className="w-[95%]">
          <ReportFrequency frequencyList={myMostFrequency} frequencyComment={`${mostFrequencyComment}`} />
        </div>
      </div>

      {/* 적게 입은 옷 */}
      <div className="mb-4">
        <ReportComment
          imageUrl="/images/report-sad.png"
          imageClass="w-24 inline-block"
          mainTitle="가장 적게 입는 옷은 무엇일까요?"
          subTitle={`가장 적게 입은 횟수는 무려 ${leastCount}회`}
        />
      </div>

      {/* 적게 입은 옷 분석 문구 */}
      <div className="flex flex-row items-center justify-center">
        <div className="w-[95%]">
          <ReportFrequency frequencyList={myLeastFrequency} frequencyComment={`${leastFrequencyComment}`} />
        </div>
      </div>

      <div className='relative flex justify-center mb-[100px]'>
        <img src="/images/cute-earth.jpg" alt="지구사진"
          className='opacity-60 z-0 rounded-2xl w-[90%] h-[300px] object-cover'
        />
          <div className="absolute flex flex-col mx-2 mb-6 px-3 py-6 rounded-2xl w-[90%]">
            <p className="mb-8 text-left text-lg font-bold whitespace-pre-wrap">{`옷장에서 잠자는 옷을\n기부해보는 건 어떠세요?`}</p>
            <div className="grid grid-cols-2 gap-4">
              <ReportFrequencyDonation
                marginRight="mr-1"
                subTitle="국내외 소외이웃을 돕는"
                storeName="아름다운 가게"
                storeLink="https://www.beautifulstore.org/"
              />
              <ReportFrequencyDonation
                subTitle="취준생에게 대여해주는"
                storeName="열린 옷장"
                storeLink="https://theopencloset.net/"
              />
              <ReportFrequencyDonation
                marginRight="mr-1"
                subTitle="옷 많아서 부담된다면"
                storeName="옷캔"
                storeLink="https://otcan.org/"
              />
              <ReportFrequencyDonation
                subTitle="장애인의 선한일터"
                storeName="굿윌 스토어"
                storeLink="https://www.goodwillstore.org/"
              />
            </div>
          </div>
        </div>
    </>
  );
};

export default ReportFrequencyPage;
