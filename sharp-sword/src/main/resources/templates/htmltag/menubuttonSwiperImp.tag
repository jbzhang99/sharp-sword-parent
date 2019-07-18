<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/Swiper/4.5.0/css/swiper.min.css">
<style>
    #.${_divId} {
        position: relative;
    }
    #${_containerId!"swiperContainer"} {
        height: 42px;
    }

    @media only screen and (min-width: 1360px) {
    #${_containerId!"swiperContainer"} {
        width: 780px;
    }
    }
    @media only screen and (min-width: 1440px) {
    #${_containerId!"swiperContainer"} {
        width: 860px;
    }
    }
    @media only screen and (min-width: 1920px) {
    #${_containerId!"swiperContainer"} {
        width: 1320px;
    }
    }
    .swiper-slide {
        text-align: center;
        /* Center slide text vertically */
        display: -webkit-box;
        display: -ms-flexbox;
        display: -webkit-flex;
        display: flex;
        -webkit-box-pack: center;
        -ms-flex-pack: center;
        -webkit-justify-content: center;
        justify-content: center;
        -webkit-box-align: center;
        -ms-flex-align: center;
        -webkit-align-items: center;
        align-items: center;
    }
    .swiper-button-next,
    .swiper-button-prev {
        width: 16px;
        height: 26px;
        background-size: unset;
        top: 76%;
        outline: none;
    }
    .swiper-button-prev {
        left: 10px;
    }
    .swiper-button-next {
        right: 10px;
    }
    .swiper-button-next.swiper-button-disabled,
    .swiper-button-prev.swiper-button-disabled {
        opacity: 0;
    }

</style>
<script src="https://cdnjs.cloudflare.com/ajax/libs/Swiper/4.5.0/js/swiper.min.js"></script>
<script type="text/javascript">
    $(function () {
        var ${_containerId!"swiperContainer"} = new Swiper('#${_containerId!"swiperContainer"}', {
            slidesPerView : 3,
            spaceBetween: 0,
            breakpoints: {
                1366: {
                    slidesPerView: ${_slidesPerView!5},
                    slidesPerGroup : ${_slidesPerGroup!1},
                },
                1440: {
                    slidesPerView: ${_slidesPerView!6},
                    slidesPerGroup : ${_slidesPerGroup!1},
                },
                1920: {
                    slidesPerView: ${_slidesPerView!8},
                    slidesPerGroup : ${_slidesPerGroup!1},
                }
            },
            navigation: {
                nextEl: '.swiper-button-next',
                prevEl: '.swiper-button-prev',
            }/*,
                on: {
                    resize: function(){
                        this.params.width = window.innerWidth - 500;
                        this.update();
                    },
                }*/
        });
    });

</script>