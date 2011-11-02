export CUDA_VERSION=4.0
export CUDA_HOME=/usr/local/cuda
export CUDA_INSTALL_PATH=${CUDA_HOME}
export CUDA_SDK_HOME=${CUDA_HOME}
export PATH=/opt/gcc44:${CUDA_HOME}/bin:$PATH:.
export LD_LIBRARY_PATH=${CUDA_HOME}/lib:${CUDA_SDK_HOME}/lib:${CUDA_SDK_HOME}/C/lib:/usr/lib/nvidia
export MANPATH=${CUDA_HOME}/man:$MANPATH

